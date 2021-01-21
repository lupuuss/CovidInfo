package pl.lodz.mobile.covidinfo.modules.plot

import io.reactivex.rxjava3.core.Scheduler
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository
import pl.lodz.mobile.covidinfo.modules.CovidTarget

class RegionalPlotPresenter(
        daysLimit: Int,
        resourcesManager: ResourcesManager,
        frontScheduler: Scheduler,
        backScheduler: Scheduler,
        private val repository: LocalCovidRepository,
        private val target: CovidTarget.RegionLevel1
) : BasePlotPresenter(daysLimit, resourcesManager, frontScheduler, backScheduler) {


    override fun refresh() {
        view?.isLoading = true
        view?.isContentVisible = false
        view?.isContentLoadingError = false

        val single = repository.getRegionsLevel1()
                .subscribeOn(backScheduler)
                .observeOn(frontScheduler)
                .doOnSuccess(::handleRegions1)
                .observeOn(backScheduler)
                .flatMap { regions1 ->
                    val id = currentRegion?.id
                            ?: regions.find { it.id == target.id }?.id
                            ?: regions.first().id

                    val region = regions1?.find { it.id == id }!!

                    currentRegion = region

                    if (currentVisibleRegion == null) currentVisibleRegion = currentRegion
                    
                    repository.getRegionsLevel2(region)
                }
                .observeOn(frontScheduler)
                .doOnSuccess(::handleRegions2)
                .observeOn(backScheduler)
                .flatMap { _ -> repository.getDailyForRegion(currentVisibleRegion!!) }
                .map { it.takeLast(daysLimit) }

        disposable = single
                .observeOn(frontScheduler)
                .subscribe(::handleDailyReport)
    }

    private fun handleRegions1(regions: List<Region>) {
        this.regions = regions

        val mapping = regions.map {
            resourcesManager.resolveRegion(it)
        }

        view?.setRegions(mapping)

        val i = regions.indexOf(currentRegion)

        if (i < 0) return

        view?.setCurrentRegion(i);
    }

    private fun handleRegions2(regions: List<Region>) {
        this.subRegions = regions

        val mapping = regions.map {
            resourcesManager.resolveRegion(it)
        }

        view?.setSubRegions(mapping)

        val i = subRegions.indexOf(currentSubRegion)

        if (i < 0) return

        view?.setCurrentSubRegion(i);
    }

    override fun pickRegion(position: Int) {
        if (regions.indexOf(currentRegion) == position) {
            return
        }

        currentRegion = regions[position]
        currentVisibleRegion = currentRegion
        refresh()
    }

    override fun pickSubRegion(position: Int) {
        if (subRegions.indexOf(currentSubRegion) == position) {
            return
        }

        currentSubRegion = subRegions[position]
        currentVisibleRegion = currentSubRegion
        refresh()
    }
}