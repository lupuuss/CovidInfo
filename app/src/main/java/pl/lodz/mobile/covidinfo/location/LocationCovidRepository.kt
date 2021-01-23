package pl.lodz.mobile.covidinfo.location

import io.reactivex.rxjava3.core.Single

interface LocationCovidRepository {

    fun getSummaryForCurrentLocation(): Single<GeoCovidData>
}