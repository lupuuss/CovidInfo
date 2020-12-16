package pl.lodz.mobile.covidinfo.modules.summary

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_summary.*
import org.koin.android.scope.currentScope
import org.koin.core.parameter.parametersOf
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import timber.log.Timber

class SummaryFragment : BaseFragment(), SummaryContract.View {

    private var allowPickingTarget: Boolean = false

    private var target: SummaryContract.Target = SummaryContract.Target.Global

    private lateinit var presenter: SummaryContract.Presenter

    private var possibleTargets: List<String> = emptyList()

    override var isPickTargetAvailable: Boolean = false
        set(value) {
            field = value
            pickTargetButton.isInvisible = !value
        }

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

            allowPickingTarget = bundle.getBoolean(allowPickingTargetBundle)
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

        presenter = currentScope.get(parameters = { parametersOf(target, allowPickingTarget) })

        presenter.init(this)

        refreshButton.setOnClickListener { presenter.refresh() }
        pickTargetButton.setOnClickListener { openPickDialog() }
    }

    private fun openPickDialog() {

        val arrayAdapter = ArrayAdapter<String>(this.context!!, android.R.layout.select_dialog_singlechoice)

        arrayAdapter.addAll(possibleTargets)

        val dialog = AlertDialog.Builder(this.context)
            .setTitle(R.string.pick_summary_target)
            .setAdapter(arrayAdapter) { dialog, position ->
                presenter.pickTarget(position)
                dialog.dismiss()
            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        dialog.show()
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

    override fun setCases(total: String?, new: String?, isPositive: Boolean) {

        totalCases.text = buildCasesString(R.string.total_cases, total, new, isPositive)
    }

    override fun setDeaths(total: String?, new: String?, isPositive: Boolean) {
        totalDeaths.text = buildCasesString(R.string.total_deaths, total, new, isPositive)
    }

    override fun setActive(total: String?, new: String?, isPositive: Boolean) {
        totalActive.text = buildCasesString(R.string.total_active, total, new, isPositive)
    }

    override fun setRecovered(total: String?, new: String?, isPositive: Boolean) {
        totalRecovered.text = buildCasesString(R.string.total_recovered, total, new, isPositive)
    }

    override fun setTargetsList(targets: List<String>) {
        this.possibleTargets = targets
    }

    override fun setSummaryName(name: String) {
        summaryTitle.text = getString(R.string.summary_name, name)
    }

    companion object {

        private const val countryNameBundle = "CountryNameBundle"
        private const val allowPickingTargetBundle = "AllowPickingTargetBundle"

        fun newInstance(
            allowPickingTarget: Boolean = false,
            target: SummaryContract.Target = SummaryContract.Target.Global
        ): SummaryFragment {

            val bundle = Bundle()

            if (target is SummaryContract.Target.Country) {
                bundle.putString(countryNameBundle, target.id)
            }

            bundle.putBoolean(allowPickingTargetBundle, allowPickingTarget)

            return SummaryFragment().apply {
                arguments = bundle
            }
        }
    }
}