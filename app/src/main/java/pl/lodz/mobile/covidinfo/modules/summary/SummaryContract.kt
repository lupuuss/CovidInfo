package pl.lodz.mobile.covidinfo.modules.summary

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView

interface SummaryContract {

    sealed class Target {
        object Global : Target()
        class Country(val id: String) : Target()
    }

    interface View : BaseView {
        fun setCases(total: String?, new: String? = null, isPositive: Boolean)
        fun setDeaths(total: String?, new: String? = null, isPositive: Boolean)
        fun setActive(total: String?, new: String? = null, isPositive: Boolean)
        fun setRecovered(total: String?, new: String? = null, isPositive: Boolean)

        var isLoading: Boolean
        var isContentVisible: Boolean
        var isContentLoadingError: Boolean
    }

    interface Presenter : BasePresenterActions<View> {

        fun refresh()
    }
}