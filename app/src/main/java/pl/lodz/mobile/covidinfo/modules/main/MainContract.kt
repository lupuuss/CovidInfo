package pl.lodz.mobile.covidinfo.modules.main

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView

interface MainContract {

    interface View : BaseView {
        fun navigateToSettings()
        fun navigateToCovidInYourArea()
        fun navigateToTwitter()
        fun navigateToWorld()
    }

    interface Presenter : BasePresenterActions<View> {
        fun goToSettings()
        fun goToCovidInYourArea()
        fun goToTwitter()
        fun goToWorld()
    }
}