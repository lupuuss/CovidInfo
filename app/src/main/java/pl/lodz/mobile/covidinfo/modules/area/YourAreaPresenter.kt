package pl.lodz.mobile.covidinfo.modules.area

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.location.GeoCovidData
import pl.lodz.mobile.covidinfo.location.LocationCovidRepository
import java.text.DecimalFormat

class YourAreaPresenter(
        private val locationCovidRepository: LocationCovidRepository,
        private val frontScheduler: Scheduler,
        private val backScheduler: Scheduler,
        private val resource: ResourcesManager,
) : BasePresenter<YourAreaContract.View>(), YourAreaContract.Presenter {

    private var disposable: Disposable? = null

    override fun init(view: YourAreaContract.View) {
        super.init(view)

        refresh()
    }

    override fun refresh() {

        view?.isLoading = true
        view?.isContentVisible = false
        view?.isContentLoadingError = false

        disposable = locationCovidRepository
                .getSummaryForCurrentLocation()
                .subscribeOn(backScheduler)
                .observeOn(frontScheduler)
                .subscribe(::handleGeoCovidData)
    }

    private fun handleGeoCovidData(geoCovidData: GeoCovidData?, throwable: Throwable?) {

        view?.isLoading = false


        if (geoCovidData == null) {

            throwable?.printStackTrace()
            view?.isContentLoadingError = true
            return
        }

        view?.setAddress(geoCovidData.address)

        view?.setRegion(resource.resolveRegion(geoCovidData.region))

        val data = geoCovidData.covidData

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

    override fun close() {
        super.close()

        disposable?.dispose()
    }
}