package pl.lodz.mobile.covidinfo.modules.plot

import io.reactivex.rxjava3.core.Scheduler
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import java.lang.IllegalStateException

class CountryPlotPresenter(
    daysLimit: Int,
    resourcesManager: ResourcesManager,
    frontScheduler: Scheduler,
    backScheduler: Scheduler,
    private val repository: CovidRepository,
    private val target: CovidTarget.Country,
) : BasePlotPresenter(daysLimit, resourcesManager, frontScheduler, backScheduler) {

    override fun refresh() {
        view?.isLoading = true
        view?.isContentVisible = false
        view?.isContentLoadingError = false

        val single = repository
            .getCountries()
            .subscribeOn(backScheduler)
            .map { cts -> cts.sortedBy { it.name } }
            .observeOn(frontScheduler)
            .doOnSuccess(::handleCountries)
            .observeOn(backScheduler)
            .flatMap { regions ->
                val id = currentRegion?.id
                    ?: regions.find { it.id == target.id }?.id
                    ?: regions.first().id

                val region = regions?.find { it.id == id }!!

                currentRegion = region
                currentVisibleRegion = region

                repository.getDailyForCountry(region)
            }.map {
                it.takeLast(daysLimit)
            }

        disposable = single
            .observeOn(frontScheduler)
            .subscribe(::handleDailyReport)
    }

    private fun handleCountries(countries: List<Region>) {
        regions = countries

        val mapping = regions.map {
            resourcesManager.resolveCountryNameBySlug(it.id)
        }

        view?.setRegions(mapping)
    }

    override fun pickRegion(position: Int) {

        currentRegion = regions[position]
        currentVisibleRegion = currentRegion
        refresh()
    }

    override fun pickSubRegion(position: Int) {
        throw IllegalStateException("Subregions not supported here!")
    }
}