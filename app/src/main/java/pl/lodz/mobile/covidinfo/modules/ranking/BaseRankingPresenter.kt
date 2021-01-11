package pl.lodz.mobile.covidinfo.modules.ranking

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto
import java.text.DecimalFormat

abstract class BaseRankingPresenter(
        protected val resourcesManager: ResourcesManager,
        protected val frontScheduler: Scheduler,
        protected val backScheduler: Scheduler,
        private val limit: Int
): BasePresenter<RankingContract.View>(), RankingContract.Presenter {

    protected var disposable: Disposable? = null

    private lateinit var properties: List<CovidPropertyDto>

    protected var currentProperty: CovidProperty = CovidProperty.TotalCases

    private val decimalFormat = DecimalFormat("#,##0")

    override fun init(view: RankingContract.View) {
        super.init(view)

        properties = CovidProperty.values().map {
            CovidPropertyDto(
                CovidPropertyDto.Name.valueOf(it.name),
                resourcesManager.resolveProperty(it),
            )
        }

        val currentPropertyDto = properties.find { it.name.name == currentProperty.name }

        view.setPossibleProperties(properties)
        view.setCurrentProperty(currentPropertyDto!!)

        refresh()
    }

    override fun close() {
        super.close()
        disposable?.dispose()
    }

    protected fun handleRankingResponse(data: List<Pair<String, Int?>>?, error: Throwable?) {

        view?.isLoading = false

        if (data == null) {
            view?.isContentVisible = false
            view?.isContentLoadingError = true

            error?.printStackTrace()
            return
        }

        view?.isContentVisible = true
        view?.isContentLoadingError = false

        val limited = if (limit != 0) data.take(limit) else data

        view?.clearRanking()

        limited.forEach {
            view?.addNextPositionToRanking(it.first, formatNumber(it.second))
        }
    }

    private fun formatNumber(number: Int?): String {
        return decimalFormat.format(number ?: 0)
    }

    override fun pickedProperty(propertyName: CovidPropertyDto.Name) {
        currentProperty = CovidProperty.valueOf(propertyName.name)
        refresh()
    }
}