package pl.lodz.mobile.covidinfo.model.covid.repositories

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.CovidApi
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.objects.CountryDaily
import pl.lodz.mobile.covidinfo.utility.CachedSingle
import pl.lodz.mobile.covidinfo.utility.date.DateUtils.parseAsIsoDate
import java.util.*
import java.util.concurrent.TimeUnit

class BasicCovidRepository(
    private val covidApi: CovidApi,
    private val locale: Locale,
    private val localRepositories: Map<SupportedLocals, LocalCovidRepository>,
    timeProvider: () -> Long
) : CovidRepository {

    private var countriesCached = CachedSingle(timeProvider, 12, TimeUnit.HOURS) {
        covidApi
            .getCountries()
            .map { list ->
                list.map { Region(it.slug, it.name, Region.Level.Country) }
                    .map { it.id to it }
                    .toMap()
            }
    }

    private var summariesCached = CachedSingle(timeProvider, 1, TimeUnit.HOURS) { covidApi.getSummary() }

    override fun getCountries(): Single<List<Region>> {

        return countriesCached.observable.map { it.values.toList() }
    }

    private fun CountryDaily.toCovidDaily(
        previousConfirmed: Int,
        previousDeaths: Int,
        previousActive: Int,
        previousRecovered: Int
    ): CovidDaily {

        val date = this.date.parseAsIsoDate(locale) ?: Date()

        val data = CovidData(
            this.confirmed,
            this.confirmed - previousConfirmed,
            this.deaths,
            this.deaths - previousDeaths,
            this.recovered,
            this.recovered - previousRecovered,
            this.active,
            this.active - previousActive,
        )

        return CovidDaily(
            date,
            data
        )
    }

    override fun getDailyForCountry(region: Region): Single<List<CovidDaily>> {

        return covidApi
            .getDailyByCountry(region.id)
            .map { list ->

                var previousConfirmed = 0
                var previousDeaths = 0
                var previousActive = 0
                var previousRecovered = 0

                list.map {

                    val daily = it.toCovidDaily(
                        previousConfirmed,
                        previousDeaths,
                        previousActive,
                        previousRecovered
                    )

                    previousConfirmed = it.confirmed
                    previousDeaths = it.deaths
                    previousActive = it.active
                    previousRecovered = it.recovered

                    daily
                }
            }
    }

    override fun getGlobalSummary(): Single<CovidData> {
        return summariesCached
            .observable
            .map {
                val global = it.global

                CovidData(
                    global.totalConfirmed,
                    global.newConfirmed,
                    global.totalDeaths,
                    global.newDeaths,
                    global.totalRecovered,
                    global.newRecovered
                )
            }
    }

    override fun getCountriesSummaries(): Single<Map<Region, CovidData>> {

        return summariesCached
            .observable
            .map { it.countrySummaries }
            .map { summaries ->

                val countries = countriesCached.observable.blockingGet()

                summaries
                    .filter { countries.containsKey(it.slug) }
                    .map {
                        countries[it.slug]!! to CovidData(
                            it.totalConfirmed,
                            it.newConfirmed,
                            it.totalDeaths,
                            it.newDeaths,
                            it.totalRecovered,
                            it.newRecovered
                        )
                    }.toMap()
            }
    }

    override fun getCountrySummary(region: Region): Single<CovidData> {
        return getCountriesSummaries().map { it[region] }
    }

    override fun getCountryRepository(id: String): LocalCovidRepository {

        val key = SupportedLocals.values().find { it.slug == id }

        return localRepositories[key]!!
    }
}