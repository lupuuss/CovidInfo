package pl.lodz.mobile.covidinfo.modules.ranking

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository

class LocalRankingPresenter(
        private val covidRepository: LocalCovidRepository,
        resourcesManager: ResourcesManager,
        frontScheduler: Scheduler,
        backScheduler: Scheduler,
        limit: Int
) : BaseRankingPresenter(resourcesManager, frontScheduler, backScheduler, limit) {
    override fun refresh() {

        view?.isLoading = true
        view?.isContentLoadingError = false
        view?.isContentVisible = false

        disposable = covidRepository.getRegionsLevel1()
                .flatMap { regions ->
                    Single.fromCallable {
                        regions.map {
                            it to covidRepository.getSummaryForRegion(it).blockingGet()
                        }.toMap()
                    }
                }
                .flatMap { data ->
                    val ranking = data
                            .entries
                            .map { resourcesManager.resolveRegion(it.key) to currentProperty.extractFrom(it.value) }
                            .sortedByDescending { it.second }
                            .mapIndexed { i, item -> "${i + 1}. ${item.first}" to item.second }
                            .toList()

                    Single.just(ranking)
                }.subscribeOn(backScheduler)
                .observeOn(frontScheduler)
                .subscribe(::handleRankingResponse)
    }
}