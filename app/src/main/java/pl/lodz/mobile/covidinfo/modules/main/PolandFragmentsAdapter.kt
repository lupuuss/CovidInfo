package pl.lodz.mobile.covidinfo.modules.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment

class PolandFragmentsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SummaryFragment.newInstance(false, CovidTarget.Country("poland"))
        1 -> RankingFragment.newInstance(
                limit = 0,
                allowSwitchProperty = false,
                target = CovidTarget.Country("poland")
        )
        else -> throw ArrayIndexOutOfBoundsException(position)
    }

}
