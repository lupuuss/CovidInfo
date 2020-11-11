package pl.lodz.mobile.covidinfo.modules.summary

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository

class SummaryPresenter(
    private val covidRepository: CovidRepository,
    private val target: SummaryContract.Target,
    private val frontScheduler: Scheduler,
    private val backScheduler: Scheduler
) : BasePresenter<SummaryContract.View>(), SummaryContract.Presenter {

    private var disposable: Disposable? = null

    override fun init(view: SummaryContract.View) {
        super.init(view)
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

        val single = when (target) {

            is SummaryContract.Target.Global -> covidRepository.getGlobalSummary()

            is SummaryContract.Target.Country -> covidRepository.getCountries()
                .flatMap { countries ->

                    val region = countries.find { it.id ==  target.id }!!
                    covidRepository.getCountrySummary(region)
                }
        }

        disposable = single
            .subscribeOn(backScheduler)
            .observeOn(frontScheduler)
            .subscribe(::handleSummaryResponse)
    }


    private fun handleSummaryResponse(data: CovidData?, error: Throwable?) {

        if (data == null) {

            error?.printStackTrace()
            view?.isLoading = false
            view?.isContentLoadingError = true
            view?.isContentVisible = false
            return
        }

        view?.setTotalCases(
            getTotal(data.totalCases, data.newCases),
            data.newCases?.toString()
        )

        view?.setTotalDeaths(
            getTotal(data.totalDeaths, data.newDeaths),
            data.newDeaths?.toString()
        )
        view?.setTotalRecovered(
            getTotal(data.totalRecovered, data.newRecovered),
            data.newRecovered?.toString()
        )
        view?.setTotalActive(
            getTotal(data.totalActive, data.newActive),
            data.newActive?.toString()
        )

        view?.isLoading = false
        view?.isContentLoadingError = false
        view?.isContentVisible = true
    }

    private fun getTotal(total: Int?, new: Int?): String? {

        return when {
            total == null -> {
                null
            }
            new == null -> {
                total.toString()
            }
            else -> {
                (total - new).toString()
            }
        }
    }

}