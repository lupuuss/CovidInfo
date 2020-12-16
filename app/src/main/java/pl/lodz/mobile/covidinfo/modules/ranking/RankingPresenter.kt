package pl.lodz.mobile.covidinfo.modules.ranking

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import java.text.DecimalFormat

class RankingPresenter(
        private val covidRepository: CovidRepository,
        private val resourcesManager: ResourcesManager,
        private val frontScheduler: Scheduler,
        private val backScheduler: Scheduler,
        private val limit: Int
): BasePresenter<RankingContract.View>(), RankingContract.Presenter {

    var disposable: Disposable? = null

    private lateinit var properties: List<CovidPropertyDto>

    private var currentProperty: CovidProperty = CovidProperty.TotalCases

    private val decimalFormat = DecimalFormat("#,##0")

    override fun init(view: RankingContract.View) {
        super.init(view)

        properties = CovidProperty.values().map {
            CovidPropertyDto(
                    it.name,
                    resourcesManager.resolveProperty(it)
            )
        }

        val currentPropertyDto = properties.find { it.name == currentProperty.name }

        view.setPossibleProperties(properties)
        view.setCurrentProperty(currentPropertyDto!!)

        refresh()
    }

    override fun close() {
        super.close()
        disposable?.dispose()
    }

    override fun refresh() {

        view?.isLoading = true
        view?.isContentLoadingError = false
        view?.isContentVisible = false

        disposable = covidRepository.getCountriesSummaries()
                .flatMap { data ->

                    val ranking = data.entries
                            .map { resourcesManager.resolveCountryNameBySlug(it.key.id) to currentProperty.extractFrom(it.value) }
                            .sortedByDescending { it.second }
                            .mapIndexed { i, item -> "${i + 1}. ${item.first}" to item.second }
                            .toList()

                    Single.just(ranking)
                }.subscribeOn(backScheduler)
                .observeOn(frontScheduler)
                .subscribe(::handleRankingResponse)
    }

    private fun handleRankingResponse(data: List<Pair<String, Int?>>?, error: Throwable?) {

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

    override fun setProperty(property: CovidPropertyDto) {
        currentProperty = CovidProperty.valueOf(property.name)
        refresh()
    }
}