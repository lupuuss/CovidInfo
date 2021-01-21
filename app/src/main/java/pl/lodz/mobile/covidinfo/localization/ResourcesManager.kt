package pl.lodz.mobile.covidinfo.localization

import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.data.Region

interface ResourcesManager {

    fun resolveProperty(property: CovidProperty): String

    fun resolveCountryNameBySlug(slug: String): String
    fun resolveRegion(region: Region): String
}