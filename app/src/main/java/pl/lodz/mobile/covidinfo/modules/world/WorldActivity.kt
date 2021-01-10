package pl.lodz.mobile.covidinfo.modules.world

import android.os.Bundle
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment

class WorldActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world)

        if (savedInstanceState == null) {
            addFragments()
        }
    }

    private fun addFragments() {

        val summaryFragment = SummaryFragment.newInstance(
                allowPickingTarget = true,
                target = CovidTarget.Global
        )

        val rankingFragment = RankingFragment.newInstance(
            limit = 0,
            allowSwitchProperty = true,
            customHeightDp = 300
        )

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentsContainer, summaryFragment)
                .add(R.id.fragmentsContainer, rankingFragment)
                .commit()
    }
}