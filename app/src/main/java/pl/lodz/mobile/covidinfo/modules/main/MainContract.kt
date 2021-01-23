package pl.lodz.mobile.covidinfo.modules.main

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView

interface MainContract {

    interface View : BaseView {
        fun navigateToCovidInYourArea()
        fun navigateToTwitter()
        fun navigateToWorld()
        fun navigateToPoland()
    }

    interface Presenter : BasePresenterActions<View> {
        fun goToCovidInYourArea()
        fun goToTwitter()
        fun goToWorld()
        fun goToPoland()
    }
}