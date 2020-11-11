package pl.lodz.mobile.covidinfo.modules.summary

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_summary.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment

class SummaryFragment : BaseFragment(), SummaryContract.View {

    private val presenter: SummaryContract.Presenter by currentScope.inject()

    var target: SummaryContract.Target = SummaryContract.Target.Global

    override var isLoading: Boolean = true
        set(value) {
            field = value
            progressBar.isVisible = value
        }

    override var isContentVisible: Boolean = false
        set(value) {
            field = value
            totalCases.isInvisible = !value
            totalActive.isInvisible = !value
            totalDeaths.isInvisible = !value
            totalRecovered.isInvisible = !value
        }

    override var isContentLoadingError: Boolean = false
        set(value) {
            field = value
            errorMessage.isInvisible = !value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(countryNameBundle)?.let {
                target = SummaryContract.Target.Country(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshButton.setOnClickListener { presenter.refresh() }

        presenter.init(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.close()
    }

    private fun buildCasesString(
        @StringRes statName: Int,
        total: String?,
        new: String?,
        isPositive: Boolean
    ): Spanned? {

        if (total == null) {
            return SpannableString(getString(R.string.no_data))
        }

        val color = baseActivity.getColorForAttr(
            if (isPositive) R.attr.colorPositive else R.attr.colorNegative
        )

        val colorStr = "#" + Integer.toHexString(color and 0x00ffffff)

        val finalStr = getString(
            R.string.total_stat_plus_new,
            getString(statName),
            total,
            colorStr,
            new ?: "0"
        )

        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(finalStr, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(finalStr)
        }
    }

    override fun setTotalCases(total: String?, new: String?) {

        totalCases.text = buildCasesString(R.string.total_cases, total, new, false)
    }

    override fun setTotalDeaths(total: String?, new: String?) {
        totalDeaths.text = buildCasesString(R.string.total_deaths, total, new, false)
    }

    override fun setTotalActive(total: String?, new: String?) {
        totalActive.text = buildCasesString(R.string.total_active, total, new, false)
    }

    override fun setTotalRecovered(total: String?, new: String?) {
        totalRecovered.text = buildCasesString(R.string.total_recovered, total, new, true)
    }

    companion object {

        private const val countryNameBundle = "CountryNameBundle"

        fun getInstance(target: SummaryContract.Target = SummaryContract.Target.Global): SummaryFragment {

            return if (target is SummaryContract.Target.Country) {

                val bundle = Bundle()

                bundle.putString(countryNameBundle, target.id)

                SummaryFragment().apply {
                    arguments = bundle
                }

            } else {
                SummaryFragment()
            }
        }
    }
}