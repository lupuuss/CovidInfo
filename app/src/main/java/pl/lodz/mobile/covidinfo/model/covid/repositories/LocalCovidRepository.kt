package pl.lodz.mobile.covidinfo.model.covid.repositories

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region

interface LocalCovidRepository {

    fun getRegionsLevel1(): Single<List<Region>>

    fun getRegionsLevel2(): Single<List<Region>>

    fun getRegionsLevel2(parent: Region): Single<List<Region>>

    fun getDailyForRegion(region: Region): Single<List<CovidDaily>>

    fun getSummaryForRegion(region: Region): Single<CovidData>
}