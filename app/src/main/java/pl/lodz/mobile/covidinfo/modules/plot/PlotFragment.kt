package pl.lodz.mobile.covidinfo.modules.plot

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_plot.*
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.modules.CovidPropertyDto
import pl.lodz.mobile.covidinfo.utility.dpToPixels
import timber.log.Timber
import kotlin.math.roundToInt
import kotlin.random.Random


class PlotFragment : BaseFragment(), PlotContract.View {

    private val supportedProperties = arrayOf(
        CovidPropertyDto.Name.TotalDeaths,
        CovidPropertyDto.Name.TotalCases,
        CovidPropertyDto.Name.TotalRecovered,
        CovidPropertyDto.Name.TotalActive
    )

    private val lineDataSetSettings: LineDataSet.() -> Unit = {
        lineWidth = dpToPixels(requireContext(), 0.8f).toFloat()
    }

    private val lineDataSettings: LineData.() -> Unit = {
        setDrawValues(false)
    }

    private var properties: List<CovidPropertyDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
        plot.legend.textColor = colorOnPrimary

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

        deathsButton.setOnClickListener { onClickProperty(it.id) }

        setData("Total cases", List(30) { Random.nextInt(0, 30) })

    }

    private fun onClickProperty(id: Int) {
        val propertyName = when (id) {
            R.id.deathsButton -> CovidPropertyDto.Name.TotalDeaths
            R.id.casesButton -> CovidPropertyDto.Name.TotalCases
            R.id.recoveredButton -> CovidPropertyDto.Name.TotalRecovered
            R.id.activeButton -> CovidPropertyDto.Name.TotalActive
            else -> throw IllegalArgumentException("Not supported property!")
        }

        if (properties.find { it.name == propertyName } != null) {
            // presenter.pickProperty(propertyName)
        }
    }

    @ColorInt
    private fun getAttrColor(context: Context, @AttrRes colorOnPrimary: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(colorOnPrimary, typedValue, true)
        return typedValue.data
    }

    override fun setData(title: String, data: List<Int>) {

        val set = LineDataSet(
            data.mapIndexed { i, value -> Entry(i.toFloat(), value.toFloat()) },
            title
        ).apply(lineDataSetSettings)

        val line = LineData(set).apply(lineDataSettings)

        plot.data = line
    }

    override fun setRegions(regions: List<String>) {

        region.isVisible = regions.isNotEmpty()
        region.adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            regions
        )
    }

    override fun setSubRegions(subRegions: List<String>) {
        subregion.isVisible = subRegions.isNotEmpty()
        subregion.adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            subRegions
        )
    }

    override fun setProperties(properties: List<CovidPropertyDto>) {

        for (property in properties) {

            when (property.name) {
                CovidPropertyDto.Name.TotalCases -> casesButton.isVisible = true
                CovidPropertyDto.Name.TotalDeaths -> deathsButton.isVisible = true
                CovidPropertyDto.Name.TotalRecovered -> recoveredButton.isVisible = true
                CovidPropertyDto.Name.TotalActive -> activeButton.isVisible = true
                else -> throw IllegalArgumentException("Not supported property! Supported properties: $supportedProperties")
            }
        }

        propertyButtonsFlow.isVisible = properties.isNotEmpty()

        this.properties = properties
    }
}