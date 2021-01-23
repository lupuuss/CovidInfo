package pl.lodz.mobile.covidinfo.modules.area

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.DynamicContentView

interface YourAreaContract {

    interface View : DynamicContentView {

        fun setCases(total: String?, new: String?, isPositive: Boolean)
        fun setDeaths(total: String?, new: String?, isPositive: Boolean)
        fun setActive(total: String?, new: String?, isPositive: Boolean)
        fun setRecovered(total: String?, new: String?, isPositive: Boolean)
        fun setAddress(address: String)
        fun setRegion(region: String)
    }

    interface Presenter : BasePresenterActions<View> {
        fun refresh()
    }
}