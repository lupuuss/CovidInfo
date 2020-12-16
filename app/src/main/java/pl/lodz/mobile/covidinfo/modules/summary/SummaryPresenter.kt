package pl.lodz.mobile.covidinfo.modules.summary

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import timber.log.Timber
import java.lang.IllegalStateException
import java.text.DecimalFormat
import java.util.*

class SummaryPresenter(
    private val covidRepository: CovidRepository,
    private var target: SummaryContract.Target,
    private val frontScheduler: Scheduler,
    private val backScheduler: Scheduler,
    private val allowPickingTarget: Boolean
) : BasePresenter<SummaryContract.View>(), SummaryContract.Presenter {

    private var disposable: Disposable? = null

    private val possibleTargets = TreeSet<SummaryContract.Target>().apply {
        add(SummaryContract.Target.Global)
    }

    private var currentRegion: Region? = null

    override fun init(view: SummaryContract.View) {
        super.init(view)

        view.isPickTargetAvailable = false

        refresh()
    }

    override fun pickTarget(position: Int) {
        target = possibleTargets.elementAt(position)
        refresh()
    }

    override fun close() {
        super.close()
        disposable?.dispose()
    }

    override fun refresh() {

        disposable?.dispose()

        view?.isLoading = true
        view?.isContentLoadingError = false
        view?.isContentVisible = false

        val single = covidRepository
            .getCountries()
            .subscribeOn(backScheduler)
            .observeOn(frontScheduler)
            .doOnSuccess(::handleCountries)
            .flatMap { countries ->

                when (val target = this.target) {

                    is SummaryContract.Target.Global -> {
                        this.currentRegion = Region.global
                        covidRepository.getGlobalSummary()
                    }
                    is SummaryContract.Target.Country -> {
                        val region = countries.find { it.id == target.id } ?: throw IllegalStateException("Region not found!")

                        this.currentRegion = region
                        covidRepository.getCountrySummary(region)
                    }
                }
            }

        disposable = single
            .subscribeOn(backScheduler)
            .observeOn(frontScheduler)
            .subscribe(::handleSummaryResponse)
    }

    private fun handleCountries(countries: List<Region>) {
        possibleTargets.addAll(countries.map { SummaryContract.Target.Country(it.id) })
        view?.isPickTargetAvailable = allowPickingTarget

        if (allowPickingTarget) {
            view?.setTargetsList(possibleTargets.map { it.toString() })
        }
    }

    private fun handleSummaryResponse(data: CovidData?, error: Throwable?) {

        if (data == null) {

            error?.printStackTrace()
            view?.isLoading = false
            view?.isContentLoadingError = true
            view?.isContentVisible = false
            return
        }

        view?.setCases(
            getTotal(data.totalCases, data.newCases),
            getNew(data.newCases),
            isPositive(data.newCases, false)
        )

        view?.setDeaths(
            getTotal(data.totalDeaths, data.newDeaths),
            getNew(data.newDeaths),
            isPositive(data.newDeaths, false)
        )
        view?.setRecovered(
            getTotal(data.totalRecovered, data.newRecovered),
            getNew(data.newRecovered),
            isPositive(data.newRecovered, true)
        )
        view?.setActive(
            getTotal(data.totalActive, data.newActive),
            getNew(data.newActive),
            isPositive(data.newActive, false)
        )

        view?.setSummaryName(currentRegion?.name ?: "")
        view?.isLoading = false
        view?.isContentLoadingError = false
        view?.isContentVisible = true
    }

    private fun getNew(newCases: Int?): String? {

        newCases ?: return null

        val newDataFormat = DecimalFormat("+#,##0;-#")

        return newDataFormat.format(newCases)
    }

    private fun isPositive(newCases: Int?, default: Boolean): Boolean {
        if (newCases == null) {
            return default
        }

        return if (newCases > 0) {
            default
        } else {
            !default
        }
    }

    private fun getTotal(total: Int?, new: Int?): String? {

        val formatter = DecimalFormat("#,##0")

        return when {
            total == null -> {
                null
            }
            new == null -> {
                formatter.format(total)
            }
            else -> {
                formatter.format(total - new)
            }
        }
    }

}