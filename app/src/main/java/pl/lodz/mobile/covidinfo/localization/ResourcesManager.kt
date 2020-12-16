package pl.lodz.mobile.covidinfo.localization

import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty

interface ResourcesManager {

    fun resolveProperty(property: CovidProperty): String

    fun resolveCountryNameBySlug(slug: String): String
}