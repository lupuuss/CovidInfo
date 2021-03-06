package pl.lodz.mobile.covidinfo.modules.plot

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_plot.*
import kotlinx.android.synthetic.main.fragment_plot.errorMessage
import kotlinx.android.synthetic.main.fragment_plot.progressBar
import kotlinx.android.synthetic.main.fragment_plot.refreshButton
import org.koin.android.scope.currentScope
import org.koin.core.parameter.parametersOf
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.FilteredDialog
import pl.lodz.mobile.covidinfo.utility.dpToPixels
import java.lang.IllegalStateException
import kotlin.math.roundToInt


class PlotFragment : BaseFragment(), PlotContract.View {

    private lateinit var regions: List<String>
    private lateinit var subRegions: List<String>

    private var allowTargetSwitch: Boolean = false
    private var customHeightDp: Int? = null

    private val supportedProperties = arrayOf(
        CovidPropertyDto.Name.TotalDeaths,
        CovidPropertyDto.Name.TotalCases,
        CovidPropertyDto.Name.TotalRecovered,
        CovidPropertyDto.Name.TotalActive
    )

    private lateinit var lineDataSetSettings: LineDataSet.() -> Unit

    private lateinit var lineDataSettings: LineData.() -> Unit

    private var properties: List<CovidPropertyDto> = emptyList()

    private var propertiesStates: MutableMap<CovidPropertyDto.Name, Boolean> =
        supportedProperties.map { it to false }.toMap().toMutableMap()

    private lateinit var presenter: PlotContract.Presenter

    override var isLoading: Boolean = false
        set(value) {
            field = value
            progressBar.isVisible = value
        }

    override var isContentVisible: Boolean = false
        set(value) {
            field = value
            plot.isInvisible = !value
        }

    override var isContentLoadingError: Boolean = false
        set(value) {
            field = value
            errorMessage.isVisible = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_plot, container, false) as ViewGroup

        view.layoutTransition.setAnimateParentHierarchy(false)

        if (arguments?.containsKey(customHeightDpBundle) == true) {
            customHeightDp = arguments!!.getInt(customHeightDpBundle)
        }

        customHeightDp?.let {

            view.findViewById<LineChart>(R.id.plot)
                .layoutParams
                .height = dpToPixels(requireContext(), it.toFloat())
        }

        val colorContrast = getAttrColor(requireContext(), R.attr.colorContrast)

        lineDataSetSettings =  {
            lineWidth = dpToPixels(requireContext(), 0.8f).toFloat()
            color = colorContrast
            setCircleColor(colorContrast)
            setDrawCircleHole(false)
        }

        lineDataSettings = {
            setDrawValues(false)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val limit = arguments!!.getInt(limitBundle)
        val country = arguments?.getString(countryBundle)
        val level1 = arguments?.getString(level1Bundle)

        allowTargetSwitch = arguments?.getBoolean(allowTargetSwitchBundle) ?: false

        val target = when {
            country != null && level1 != null -> {

                val (countryId, countryName) = country.split(":")
                val (level1Id, level1Name) = level1.split(":")

                CovidTarget.RegionLevel1(level1Id, CovidTarget.Country(countryId, countryName), level1Name)
            }
            country != null -> {
                val (countryId, countryName) = country.split(":")
                CovidTarget.Country(countryId, countryName)
            }
            else -> {
                throw IllegalStateException("Not supported target for PlotFragment!")
            }
        }

        presenter = currentScope.get(parameters = { parametersOf(limit, target) })

        configPlot()

        pickRegionButton.isInvisible = true
        pickSubRegionButton.isVisible = false

        pickRegionButton.setOnClickListener(::onPickRegionClick)

        pickSubRegionButton.setOnClickListener(::onPickSubRegionClick)

        listOf(deathsButton, casesButton, recoveredButton, activeButton).forEach { button ->
            button.setOnClickListener { onClickProperty(it.id) }
        }

        refreshButton.setOnClickListener { presenter.refresh() }

        presenter.init(this)

    }

    private fun onPickRegionClick(view: View?) {
        FilteredDialog.Builder(requireContext())
                .setValues(regions)
                .setOnItemSelect { position, _->

                    presenter.pickRegion(position)
                }.show()
    }

    private fun onPickSubRegionClick(view: View?) {
        FilteredDialog.Builder(requireContext())
                .setValues(subRegions)
                .setOnItemSelect { position, _->

                    presenter.pickSubRegion(position)
                }.show()
    }

    private fun configPlot() {
        val colorOnPrimary = getAttrColor(requireContext(), R.attr.colorOnPrimary)

        plot.axisLeft.textColor = colorOnPrimary
        plot.description.text = ""
        plot.axisRight.textColor = colorOnPrimary

        plot.axisRight.granularity = 1f
        plot.axisLeft.granularity = 1f

        plot.xAxis.textColor = colorOnPrimary

        // it results in (labelCount - 1) labels because we skip first and last label (one of them is default)
        plot.xAxis.labelCount = 5

        plot.xAxis.position = XAxis.XAxisPosition.BOTTOM
        plot.xAxis.isGranularityEnabled = true
        plot.legend.isEnabled = false

        plot.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {

                val intValue = value.roundToInt()

                if (intValue == 0 || intValue == plot.lineData.dataSets[0].entryCount - 1) {
                    return ""
                }

                return getString(
                    R.string.days_ago,
                    (plot.lineData.dataSets[0].entryCount - intValue).toString()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.close()
    }

    private fun onClickProperty(id: Int) {
        val propertyName = when (id) {
            R.id.deathsButton -> {
                CovidPropertyDto.Name.TotalDeaths
            }
            R.id.casesButton -> CovidPropertyDto.Name.TotalCases
            R.id.recoveredButton -> CovidPropertyDto.Name.TotalRecovered
            R.id.activeButton -> CovidPropertyDto.Name.TotalActive
            else -> throw IllegalArgumentException("Not supported property!")
        }

        if (properties.find { it.name == propertyName } != null) {
            presenter.pickProperty(propertyName)
        }
    }

    @Suppress("SameParameterValue")
    @ColorInt
    private fun getAttrColor(context: Context, @AttrRes colorOnPrimary: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(colorOnPrimary, typedValue, true)
        return typedValue.data
    }

    override fun setCurrentProperty(property: CovidPropertyDto.Name) {

        propertiesStates =
            supportedProperties.map { it to false }.toMap().toMutableMap()

        propertiesStates[property] = true

        invalidateIcons()
    }

    private fun invalidateIcons() {

        for ((name, state) in propertiesStates) {

            when (name) {
                CovidPropertyDto.Name.TotalCases -> updateIcon(casesButton, state)
                CovidPropertyDto.Name.TotalDeaths -> updateIcon(deathsButton, state)
                CovidPropertyDto.Name.TotalRecovered -> updateIcon(recoveredButton, state)
                CovidPropertyDto.Name.TotalActive -> updateIcon(activeButton, state)
            }

        }
    }

    private fun updateIcon(button: FloatingActionButton, state: Boolean) {

        val color = getAttrColor(requireContext(),  if (state) R.attr.colorOnPrimary else R.attr.colorOnPrimaryTransparent)

        button.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun setData(title: String, data: List<Int>) {

        val set = LineDataSet(
            data.mapIndexed { i, value -> Entry(i.toFloat(), value.toFloat()) },
            title
        ).apply(lineDataSetSettings)

        val line = LineData(set).apply(lineDataSettings)

        plotTitle.text = title

        plot.data = line

        plot.invalidate()
    }

    override fun setTitle(title: String) {
        regionName.text = title
    }

    override fun setRegions(regions: List<String>) {

        if (!allowTargetSwitch) return

        this.regions = regions
        this.pickRegionButton.isInvisible = regions.isEmpty()
    }

    override fun setSubRegions(subRegions: List<String>) {

        if (!allowTargetSwitch) return

        this.subRegions = subRegions
        this.pickSubRegionButton.isVisible = subRegions.isNotEmpty()
    }

    override fun setProperties(properties: List<CovidPropertyDto>) {

        for (property in supportedProperties) {

            val show = properties.find { it.name == property } != null

            when (property) {
                CovidPropertyDto.Name.TotalCases ->  setProperState(casesButton, show)
                CovidPropertyDto.Name.TotalDeaths -> setProperState(deathsButton, show)
                CovidPropertyDto.Name.TotalRecovered -> setProperState(recoveredButton, show)
                CovidPropertyDto.Name.TotalActive -> setProperState(activeButton, show)
            }
        }

        if (propertyButtonsFlow.isVisible != properties.isNotEmpty())
            propertyButtonsFlow.isVisible = properties.isNotEmpty()

        this.properties = properties
    }

    private fun setProperState(button: FloatingActionButton, show: Boolean) {

        if (button.isVisible == show) return

        if (show) {
            button.show()
        } else {
            button.hide()
        }

    }

    companion object {

        private const val limitBundle = "LimitBundle"
        private const val countryBundle = "CountryBundle"
        private const val level1Bundle = "Level1Bundle"
        private const val allowTargetSwitchBundle = "AllowTargetSwitchBundle"
        private const val customHeightDpBundle = "CustomHeightDpBundle"

        fun newInstance(
            limit: Int,
            defaultTarget: CovidTarget,
            allowTargetSwitch: Boolean,
            customHeightDp: Int? = null
        ): PlotFragment {

            val args = Bundle().apply {
                putInt(limitBundle, limit)
                putBoolean(allowTargetSwitchBundle, allowTargetSwitch)
                if (defaultTarget is CovidTarget.Country) {
                    putString(countryBundle, defaultTarget.id + ":" + defaultTarget.name)
                }

                if (defaultTarget is CovidTarget.RegionLevel1) {
                    putString(countryBundle, defaultTarget.country.id + ":" + defaultTarget.country.name)
                    putString(level1Bundle, defaultTarget.id + ":" + defaultTarget.name)
                }

                customHeightDp?.let {
                    putInt(customHeightDpBundle, it)
                }
            }

            val fragment = PlotFragment()
            fragment.arguments = args
            return fragment
        }
    }
}