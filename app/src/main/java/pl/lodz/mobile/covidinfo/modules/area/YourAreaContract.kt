package pl.lodz.mobile.covidinfo.modules.area

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.DynamicContentView

interface YourAreaContract {

    interface View : DynamicContentView {

    }

    interface Presenter : BasePresenterActions<View> {

    }
}