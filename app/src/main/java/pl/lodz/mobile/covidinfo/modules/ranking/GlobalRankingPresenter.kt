package pl.lodz.mobile.covidinfo.modules.ranking

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import java.text.DecimalFormat

class GlobalRankingPresenter(
        private val covidRepository: CovidRepository,
        resourcesManager: ResourcesManager,
        frontScheduler: Scheduler,
        backScheduler: Scheduler,
        limit: Int
): BaseRankingPresenter(resourcesManager, frontScheduler, backScheduler, limit) {

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


}