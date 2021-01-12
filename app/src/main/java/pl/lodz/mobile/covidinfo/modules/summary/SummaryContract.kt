package pl.lodz.mobile.covidinfo.modules.summary

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView
import pl.lodz.mobile.covidinfo.base.DynamicContentView

interface SummaryContract {

    interface View : DynamicContentView {

        fun setCases(total: String?, new: String? = null, isPositive: Boolean)

        fun setDeaths(total: String?, new: String? = null, isPositive: Boolean)

        fun setActive(total: String?, new: String? = null, isPositive: Boolean)

        fun setRecovered(total: String?, new: String? = null, isPositive: Boolean)

        fun setTargetsList(targets: List<String>)

        fun setSummaryName(name: String)

        var isPickTargetAvailable: Boolean
    }

    interface Presenter : BasePresenterActions<View> {

        fun refresh()
        fun pickTarget(position: Int)
    }
}