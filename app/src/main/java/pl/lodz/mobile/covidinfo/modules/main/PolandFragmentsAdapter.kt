package pl.lodz.mobile.covidinfo.modules.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.plot.PlotFragment
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment

class PolandFragmentsAdapter(
        manager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position) {

        0 -> PlotFragment.newInstance(
                limit = 30,
                defaultTarget = CovidTarget.Country.Poland,
                allowTargetSwitch = true
        )
        1 -> SummaryFragment.newInstance(false, CovidTarget.Country.Poland)
        2 -> RankingFragment.newInstance(
                limit = 0,
                allowSwitchProperty = false,
                target = CovidTarget.Country.Poland
        )
        3 -> PlotFragment.newInstance(
                limit = 30,
                defaultTarget = CovidTarget.RegionLevel1.Mazowieckie,
                allowTargetSwitch = false
        )

        else -> throw ArrayIndexOutOfBoundsException(position)
    }

}
