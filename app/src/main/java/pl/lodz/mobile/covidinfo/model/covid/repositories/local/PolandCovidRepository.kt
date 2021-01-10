package pl.lodz.mobile.covidinfo.model.covid.repositories.local

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.CovidPlApi
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects.DistrictsDailyData
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects.DistrictsData
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects.ProvincesDailyDetails
import pl.lodz.mobile.covidinfo.utility.CachedSingle
import pl.lodz.mobile.covidinfo.utility.date.DateUtils.parseAsIsoDate
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.TimeUnit

class PolandCovidRepository(
    private val covidPlApi: CovidPlApi,
    timeProvider: () -> Long,
    private val locale: Locale,
) : LocalCovidRepository {

    private val provincesCached = CachedSingle(timeProvider, 12, TimeUnit.HOURS) {
        covidPlApi.getProvinces()
    }

    private val provDailyDeathsCache = CachedSingle(timeProvider, 1, TimeUnit.HOURS) {
        covidPlApi.getDailyProvincesDeaths()
    }

    private val provDailyCasesCache = CachedSingle(timeProvider, 1, TimeUnit.HOURS) {
        covidPlApi.getDailyProvincesCases()
    }

    private val provDailyRecoveredCache = CachedSingle(timeProvider, 1, TimeUnit.HOURS) {
        covidPlApi.getDailyProvincesRecovered()
    }

    private val districtsDailyCached = CachedSingle(timeProvider, 1, TimeUnit.HOURS) {
        covidPlApi.getDailyDistricts()
    }

    override fun getRegionsLevel1(): Single<List<Region>> {

        return provincesCached.observable.map { provincesSummaries ->
            provincesSummaries.map { Region(it.name, it.name, Region.Level.AdministrationLevel1) }
        }
    }

    override fun getRegionsLevel2(): Single<List<Region>> {

        return districtsDailyCached
            .observable
            .map { districts ->
                districts.map {
                    Region("${it.provinceName}:${it.name}",
                        it.districtName, Region.Level.AdministrationLevel2)
                }
            }
    }

    override fun getDailyForRegion(region: Region): Single<List<CovidDaily>> {

        return when (region.level) {
            Region.Level.AdministrationLevel1 -> getDailyForLevel1(region)
            Region.Level.AdministrationLevel2 -> getDailyForLevel2(region)
            else -> throw IllegalArgumentException("Region: ${region.level} is not supported here!")
        }
    }

    private fun getDailyForLevel1(region: Region): Single<List<CovidDaily>> {

        return Single.zip(
                provDailyDeathsCache.observable,
                provDailyCasesCache.observable,
                provDailyRecoveredCache.observable
        ) { deathsDaily, casesDaily, recoveredDaily ->
            mapDailyLevel1(deathsDaily, casesDaily, recoveredDaily, region)
        }
    }

    private fun mapDailyLevel1(
        deathsDaily: List<ProvincesDailyDetails>,
        casesDaily: List<ProvincesDailyDetails>,
        recoveredDaily: List<ProvincesDailyDetails>,
        region: Region
    ): List<CovidDaily> {
        var prevDeaths = 0
        var prevCases = 0
        var prevRecovered = 0

        return deathsDaily
            .zip(casesDaily)
            .zip(recoveredDaily)
            .map { combined ->
                val (deathsProv, casesProv) = combined.first
                val recoveredProv = combined.second

                val date = deathsProv.date.parseAsIsoDate(locale) ?: Date()

                val deaths = deathsProv.getByName(region.id)
                val newDeaths = deaths - prevDeaths
                prevDeaths = deaths

                val cases = casesProv.getByName(region.id)
                val newCases = cases - prevCases
                prevCases = cases

                val recovered = recoveredProv.getByName(region.id)
                val newRecovered = recovered - prevRecovered
                prevRecovered = recovered


                val covidData = CovidData(
                    cases,
                    newCases,
                    deaths,
                    newDeaths,
                    recovered,
                    newRecovered
                )

                CovidDaily(date, covidData)
            }
    }

    private fun getDailyForLevel2(region: Region): Single<List<CovidDaily>> {

        val (provinceName, districtName) = extractNames(region)

        return districtsDailyCached
            .observable
            .map { districtsData ->
                findByProvinceAndDistrict(districtsData, provinceName, districtName)
            }.map { data ->
                data ?: return@map emptyList()

                data.dailyData.map {
                    it.toCovidDaily(locale)
                }
            }
    }

    private fun extractNames(region: Region): Pair<String, String> {
        val provinceName = region.id.substringBefore(":")
        val districtName = region.id.substringAfter(":")

        return provinceName to districtName
    }

    private fun findByProvinceAndDistrict(
        list: List<DistrictsData>,
        provinceName: String,
        districtName: String
    ): DistrictsData? = list.find { it.provinceName == provinceName && it.name == districtName }

    private fun DistrictsDailyData.toCovidDaily(locale: Locale): CovidDaily {
        return CovidDaily(
            date.parseAsIsoDate(locale) ?: Date(),
            CovidData(
                totalCases = totalCases,
                newCases = todayCases,
                totalDeaths = totalDeaths,
                newDeaths = todayDeaths
            )
        )
    }

    override fun getSummaryForRegion(region: Region): Single<CovidData> {
        return when (region.level) {
            Region.Level.AdministrationLevel1 -> getSummaryForLevel1(region)
            Region.Level.AdministrationLevel2 -> getSummaryForLevel2(region)
            else -> throw IllegalArgumentException("Region: ${region.level} is not supported here!")
        }
    }

    private fun getSummaryForLevel1(region: Region): Single<CovidData> {
        return Single.zip(
            provDailyDeathsCache.observable,
            provDailyCasesCache.observable,
            provDailyRecoveredCache.observable
        ) { deathsProv, casesProv, recoveredProv ->

            if (deathsProv.isEmpty()) return@zip CovidData()

            val deaths = deathsProv.last().getByName(region.id)
            val cases = casesProv.last().getByName(region.id)
            val recovered = recoveredProv.last().getByName(region.id)

            if (deathsProv.size == 1) return@zip CovidData(
                cases, cases,
                deaths, deaths,
                recovered, recovered
            )

            val newDeaths = deaths - deathsProv[deathsProv.size - 2].getByName(region.id)
            val newCases = cases - casesProv[casesProv.size - 2].getByName(region.id)
            val newRecovered = recovered - recoveredProv[recoveredProv.size - 2].getByName(region.id)

            return@zip CovidData(
                cases,
                newCases,
                deaths,
                newDeaths,
                recovered,
                newRecovered
            )
        }
    }

    private fun getSummaryForLevel2(region: Region): Single<CovidData> {

        val (provinceName, districtName) = extractNames(region)

        return districtsDailyCached.observable.map {
            findByProvinceAndDistrict(it, provinceName, districtName)
        }.map {
            it ?: return@map CovidData()

            it.dailyData
                .last()
                .toCovidDaily(locale)
                .covidData
        }
    }
}