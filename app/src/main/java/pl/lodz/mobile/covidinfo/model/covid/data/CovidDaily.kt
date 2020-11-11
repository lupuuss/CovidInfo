package pl.lodz.mobile.covidinfo.model.covid.data

import java.util.*

data class CovidDaily(
        val date: Date,
        val covidData: CovidData
)