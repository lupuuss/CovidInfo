package pl.lodz.mobile.covidinfo.modules.summary

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView

interface SummaryContract {

    sealed class Target {
        object Global : Target()
        class Country(val id: String) : Target()
    }

    interface View : BaseView {
        fun setTotalCases(total: String?, new: String? = null)
        fun setTotalDeaths(total: String?, new: String? = null)
        fun setTotalActive(total: String?, new: String? = null)
        fun setTotalRecovered(total: String?, new: String? = null)

        var isLoading: Boolean
        var isContentVisible: Boolean
        var isContentLoadingError: Boolean
    }

    interface Presenter : BasePresenterActions<View> {

        fun refresh()
    }
}