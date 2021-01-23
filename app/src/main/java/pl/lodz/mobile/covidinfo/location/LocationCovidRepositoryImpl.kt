package pl.lodz.mobile.covidinfo.location

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.location.retrofit.CodezapApi
import pl.lodz.mobile.covidinfo.location.retrofit.GeoCoding
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository
import timber.log.Timber
import java.util.*

class LocationCovidRepositoryImpl(
        private val globalRepository: CovidRepository,
        private val localRepositories: Map<String, LocalCovidRepository>,
        private val locationProvider: LocationProvider,
        private val codezapApi: CodezapApi,
        private val locale: Locale
) : LocationCovidRepository {

    override fun getSummaryForCurrentLocation(): Single<GeoCovidData> {

        return locationProvider
                .getLastKnownLocation()
                .flatMap { codezapApi.reverse(it.lat, it.lng) }
                .flatMap { globalGeo ->

                    val localGeo = codezapApi.reverse(
                            globalGeo.lat,
                            globalGeo.lng,
                            globalGeo.address.countryCode
                    ).blockingGet()

                    Single.just(globalGeo to localGeo)
                }
                .flatMap { (global, local) ->
                    val country = global.address.countryCode

                    if (localRepositories.containsKey(country)) {
                        tryGetLocalData(localRepositories[country]!!, local, global)
                    } else {
                        getGlobalData(global)
                    }
                }
    }

    private fun tryGetLocalData(
            localCovidRepository: LocalCovidRepository,
            local: GeoCoding,
            global: GeoCoding
    ): Single<GeoCovidData> {
        val levels = local
                .displayName
                .split(",")
                .map { it.trim() }

        val countryIndex = levels.indexOf(local.address.country)

        if (countryIndex < 0 || levels.size < 4) return getGlobalData(local)

        val move = if (levels[countryIndex - 1][0].isDigit()) 1 else 0

        val level1 = levels[countryIndex - 1 - move]

        val level2 = levels[countryIndex - 2 - move]

        Timber.d(levels.toString())

        Timber.d("$level1 $level2")

        return localCovidRepository
                .getRegionsLevel2()
                .flatMapMaybe { regions -> findRegion(regions, level2) }
                .switchIfEmpty(
                        localCovidRepository
                                .getRegionsLevel1()
                                .flatMapMaybe { regions -> findRegion(regions, level1) }
                ).flatMapSingle { region ->

                    val data = localCovidRepository
                            .getSummaryForRegion(region)
                            .blockingGet()

                    Single.just(region to data)
                }
                .map { (region, data) ->
                    GeoCovidData(LatLng(local.lat, local.lng), local.displayName, region, data)
                }.switchIfEmpty(getGlobalData(global))
    }

    private fun findRegion(regions: List<Region>, query: String): Maybe<Region> {
        val region = regions.find {
            it.id.contains(query, ignoreCase = true) ||
                    query.contains(it.id, ignoreCase = true)
        }

        return region?.let { Maybe.just(it) } ?: Maybe.empty()
    }

    private fun getGlobalData(global: GeoCoding): Single<GeoCovidData> {

        val slug = global
                .address
                .country
                .replace(" ", "-")
                .toLowerCase(locale)

        return globalRepository
                .getCountries()
                .flatMapMaybe { regions -> findRegion(regions, slug) }
                .flatMapSingle { region ->
                    val data = globalRepository
                            .getCountrySummary(region)
                            .blockingGet()

                    Single.just(region to data)
                }.map { (region, data) ->
                    GeoCovidData(LatLng(global.lat, global.lng), global.displayName, region, data)
                }
                .toSingle()
    }
}