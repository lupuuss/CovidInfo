package pl.lodz.mobile.covidinfo.location

import io.reactivex.rxjava3.core.Single

interface LocationProvider {

    fun getLastKnownLocation(): Single<LatLng>
}