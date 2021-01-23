package pl.lodz.mobile.covidinfo.modules.area

import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.location.LocationProvider

class YourAreaPresenter(
    private val locationProvider: LocationProvider
) : BasePresenter<YourAreaContract.View>(), YourAreaContract.Presenter {

    override fun init(view: YourAreaContract.View) {
        super.init(view)

        refresh()
    }

    private fun refresh() {

        locationProvider.getLastKnownLocation()
    }
}