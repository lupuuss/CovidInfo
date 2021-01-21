package pl.lodz.mobile.covidinfo.modules.plot

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.DynamicContentView
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto

interface PlotContract {

    interface View : DynamicContentView {

        fun setRegions(regions: List<String>)

        fun setSubRegions(subRegions: List<String>)

        fun setProperties(properties: List<CovidPropertyDto>)

        fun setData(title: String, data: List<Int>)

        fun setTitle(title: String)
    }

    interface Presenter : BasePresenterActions<View> {

        fun pickRegion(position: Int)

        fun pickSubRegion(position: Int)

        fun pickProperty(property: CovidPropertyDto.Name)

        fun refresh()
    }
}