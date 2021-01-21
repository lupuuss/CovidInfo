package pl.lodz.mobile.covidinfo.localization

import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.modules.CovidTarget

interface ResourcesManager {

    fun resolveProperty(property: CovidProperty): String
    fun resolveRegion(region: Region): String
    fun resolveTarget(target: CovidTarget): String
}