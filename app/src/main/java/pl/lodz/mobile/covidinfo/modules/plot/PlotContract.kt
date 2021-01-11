package pl.lodz.mobile.covidinfo.modules.plot

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto

interface PlotContract {

    interface View : BaseView {

        fun setRegions(regions: List<String>)

        fun setSubRegions(subRegions: List<String>)

        fun setProperties(properties: List<CovidPropertyDto>)

        fun setData(title: String, data: List<Int>)
    }

    interface Presenter : BasePresenterActions<View> {

        fun pickRegion(region: String)

        fun pickSubRegion(region: String)

        fun pickProperty(property: CovidPropertyDto.Name)
    }
}