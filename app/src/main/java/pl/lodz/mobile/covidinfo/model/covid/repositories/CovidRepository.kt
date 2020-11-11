package pl.lodz.mobile.covidinfo.model.covid.repositories

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region

interface CovidRepository {

    fun getCountries(): Single<List<Region>>

    fun getDailyForCountry(region: Region): Single<List<CovidDaily>>

    fun getGlobalSummary(): Single<CovidData>

    fun getCountriesSummaries(): Single<Map<Region, CovidData>>

    fun getCountrySummary(region: Region): Single<CovidData>

    fun getCountryRepository(region: Region): LocalCovidRepository
}