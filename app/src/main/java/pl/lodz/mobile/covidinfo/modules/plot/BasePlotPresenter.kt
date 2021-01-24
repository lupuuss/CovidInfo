package pl.lodz.mobile.covidinfo.modules.plot

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto
import timber.log.Timber

abstract class BasePlotPresenter(
    protected val daysLimit: Int,
    protected val resourcesManager: ResourcesManager,
    protected val frontScheduler: Scheduler,
    protected val backScheduler: Scheduler,
    ) : BasePresenter<PlotContract.View>(), PlotContract.Presenter {

    protected var disposable: Disposable? = null

    private val properties: List<CovidProperty> = listOf(
        CovidProperty.TotalCases,
        CovidProperty.TotalDeaths,
        CovidProperty.TotalRecovered,
        CovidProperty.TotalActive
    )


    protected var regions: List<Region> = emptyList()

    protected var subRegions: List<Region> = emptyList()

    protected var currentRegion: Region? = null

    protected var currentSubRegion: Region? = null

    protected var currentVisibleRegion: Region? = null

    private var currentProperty: CovidProperty = properties.first()

    private var lastDaily: List<CovidDaily>? = null

    override fun init(view: PlotContract.View) {
        super.init(view)

        refresh()
    }

    private fun CovidProperty.toDto(): CovidPropertyDto {
        return CovidPropertyDto(
                CovidPropertyDto.Name.valueOf(name),
                resourcesManager.resolveProperty(this)
        )
    }

    override fun close() {
        super.close()
        disposable?.dispose()
    }

    protected fun handleDailyReport(daily: List<CovidDaily>?, throwable: Throwable?) {

        view?.isLoading = false

        currentVisibleRegion?.let {
            view?.setTitle(resourcesManager.resolveRegion(it))
        } ?: Timber.d("XDDDDDDDDDDDDDDDDDDD")

        if (daily == null || daily.isEmpty()) {
            view?.isContentVisible = false
            view?.isContentLoadingError = true

            view?.setProperties(emptyList())

            throwable?.printStackTrace()
            return
        }

        val properties = listAvailableProperties(daily)

        view?.setProperties(properties)

        lastDaily = daily
        setDataToView(daily)

        view?.isContentVisible = true
        view?.isContentLoadingError = false
    }

    private fun listAvailableProperties(daily: List<CovidDaily>): List<CovidPropertyDto> {

        val properties = mutableListOf<CovidProperty>()
        val data = daily.first().covidData

        if (data.totalActive != null) properties.add(CovidProperty.TotalActive)
        if (data.totalCases != null) properties.add(CovidProperty.TotalCases)
        if (data.totalDeaths != null) properties.add(CovidProperty.TotalDeaths)
        if (data.totalRecovered != null) properties.add(CovidProperty.TotalRecovered)

        return properties.map { it.toDto() }
    }

    private fun setDataToView(daily: List<CovidDaily>) {

        view?.setCurrentProperty(currentProperty.toDto().name)

        view?.setData(
            resourcesManager.resolveProperty(currentProperty),
            daily.map { currentProperty.extractFrom(it.covidData) ?: 0 }
        )
    }

    override fun pickProperty(property: CovidPropertyDto.Name) {
        val prop = CovidProperty.valueOf(property.name)

        if (!properties.contains(prop)) {
            throw IllegalArgumentException("Not supported property!")
        }

        currentProperty = prop

        lastDaily?.let {
            setDataToView(it)
        }
    }
}