package pl.lodz.mobile.covidinfo.modules.main

import pl.lodz.mobile.covidinfo.base.BasePresenter

class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {

    override fun goToCovidInYourArea() {
        view?.navigateToCovidInYourArea()
    }

    override fun goToTwitter() {
        view?.navigateToTwitter()
    }

    override fun goToWorld() {
        view?.navigateToWorld()
    }

    override fun goToPoland() {
        view?.navigateToPoland()
    }
}