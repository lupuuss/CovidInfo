package pl.lodz.mobile.covidinfo.location

import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region

data class GeoCovidData(
        val latLng: LatLng,
        val address: String,
        val region: Region,
        val covidData: CovidData
)