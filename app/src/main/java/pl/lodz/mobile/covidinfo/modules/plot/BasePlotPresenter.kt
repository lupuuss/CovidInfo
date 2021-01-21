package pl.lodz.mobile.covidinfo.modules.plot

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidProperty
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto

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

    private lateinit var propertiesDto: List<CovidPropertyDto>

    protected var regions: List<Region> = emptyList()

    protected var subRegions: List<Region> = emptyList()

    protected var currentRegion: Region? = null

    protected var currentSubRegion: Region? = null

    protected var currentVisibleRegion: Region? = null

    private var currentProperty: CovidProperty = properties.first()

    private var lastDaily: List<CovidDaily>? = null

    override fun init(view: PlotContract.View) {
        super.init(view)

        propertiesDto = properties.map {
            CovidPropertyDto(
                CovidPropertyDto.Name.valueOf(it.name),
                resourcesManager.resolveProperty(it)
            )
        }

        refresh()
    }

    override fun close() {
        super.close()
        disposable?.dispose()
    }

    protected fun handleDailyReport(daily: List<CovidDaily>?, throwable: Throwable?) {

        view?.isLoading = false

        if (daily == null) {
            view?.isContentVisible = false
            view?.isContentLoadingError = true

            view?.setProperties(emptyList())

            throwable?.printStackTrace()
            return
        }

        view?.setProperties(propertiesDto)

        view?.setTitle(resourcesManager.resolveRegion(currentVisibleRegion!!))

        lastDaily = daily
        setDataToView(daily)

        view?.isContentVisible = true
        view?.isContentLoadingError = false
    }

    private fun setDataToView(daily: List<CovidDaily>) {
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