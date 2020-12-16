package pl.lodz.mobile.covidinfo.modules.ranking

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.DynamicContentView
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty

interface RankingContract {

    interface View : DynamicContentView {

        fun setPossibleProperties(properties: List<CovidPropertyDto>)
        fun addNextPositionToRanking(name: String, value: String)
        fun clearRanking()
        fun setCurrentProperty(currentPropertyDto: CovidPropertyDto)
    }

    interface Presenter : BasePresenterActions<View> {

        fun refresh()
        fun setProperty(property: CovidPropertyDto)
    }
}