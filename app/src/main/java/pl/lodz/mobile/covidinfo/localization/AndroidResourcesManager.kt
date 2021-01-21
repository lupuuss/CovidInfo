package pl.lodz.mobile.covidinfo.localization

import android.content.Context
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.modules.CovidTarget

class AndroidResourcesManager(private val context: Context) : ResourcesManager {

    override fun resolveProperty(property: CovidProperty): String {
        val id = when (property) {
            CovidProperty.TotalCases -> R.string.total_cases
            CovidProperty.Cases -> R.string.cases
            CovidProperty.TotalDeaths -> R.string.total_deaths
            CovidProperty.Deaths -> R.string.deaths
            CovidProperty.TotalRecovered -> R.string.total_recovered
            CovidProperty.Recovered -> R.string.recovered
            CovidProperty.TotalActive -> R.string.total_active
            CovidProperty.Active -> R.string.active
        }

        return context.getString(id)
    }

    override fun resolveRegion(region: Region): String {
        return region.name
    }

    override fun resolveTarget(target: CovidTarget): String {
        return target.toString()
    }
}