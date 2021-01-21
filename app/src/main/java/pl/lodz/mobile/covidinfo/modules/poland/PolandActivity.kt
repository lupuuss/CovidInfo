package pl.lodz.mobile.covidinfo.modules.poland

import android.os.Bundle
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.plot.PlotFragment
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment

class PolandActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poland)
        if (savedInstanceState == null) {
            addFragments()
        }
    }

    private fun addFragments() {

        val summaryFragment = SummaryFragment.newInstance(
            allowPickingTarget = true,
            target = CovidTarget.Country("poland")
        )

        val rankingFragment = RankingFragment.newInstance(
            limit = 0,
            allowSwitchProperty = true,
            customHeightDp = 300,
            CovidTarget.Country("poland")
        )

        val plotFragment = PlotFragment.newInstance(
            30,
            CovidTarget.RegionLevel1("Mazowieckie", CovidTarget.Country("poland")),
            allowTargetSwitch = true,
            customHeightDp = 200,
        )

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentsContainer, summaryFragment)
            .add(R.id.fragmentsContainer, rankingFragment)
            .add(R.id.fragmentsContainer, plotFragment)
            .commit()
    }
}