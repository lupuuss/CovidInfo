package pl.lodz.mobile.covidinfo.modules.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.plot.PlotFragment
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment

class WorldFragmentsAdapter(
        manager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SummaryFragment.newInstance(
                allowPickingTarget = false,
                target = CovidTarget.Global
        )
        1 -> RankingFragment.newInstance(
                limit = 10,
                allowSwitchProperty = false,
        )
        2 -> PlotFragment.newInstance(
            limit = 30,
            defaultTarget = CovidTarget.Country.Germany,
            allowTargetSwitch = false
        )
        3 -> PlotFragment.newInstance(
            30,
            CovidTarget.Country.Spain,
            allowTargetSwitch = false
        )
        else -> throw ArrayIndexOutOfBoundsException(position)
    }
}