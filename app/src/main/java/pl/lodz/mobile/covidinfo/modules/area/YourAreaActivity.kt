package pl.lodz.mobile.covidinfo.modules.area

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.core.text.toSpanned
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_your_area.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.location.AndroidLocationProvider
import pl.lodz.mobile.covidinfo.location.LocationProvider
import pl.lodz.mobile.covidinfo.utility.getColorForAttr


class YourAreaActivity : BaseActivity(), YourAreaContract.View {

    override var isLoading: Boolean = false
        set(value) {
            field = value
            progressBar.isVisible = value
        }
    override var isContentVisible: Boolean = false
        set(value) {
            field = value
            addressFlow.isVisible = value
            dataFlow.isVisible = value
            regionFlow.isVisible = value
        }
    override var isContentLoadingError: Boolean = false
        set(value) {
            field = value
            errorMessage.isVisible = value
        }

    private val locationProvider: LocationProvider by currentScope.inject()
    private val androidLocationProvider: AndroidLocationProvider = locationProvider as AndroidLocationProvider
    private val presenter: YourAreaContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_area)

        androidLocationProvider.init(this)

        presenter.init(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.refresh_only_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.refresh -> {
                presenter.refresh()
            }
        }

        return true
    }

    private fun buildCasesString(
            @StringRes statName: Int,
            total: String?,
            new: String?,
            isPositive: Boolean
    ): Spanned? {

        val color = getColorForAttr(if (isPositive) R.attr.colorPositive else R.attr.colorNegative)

        if (total == null) {
            return (getString(statName) + ": -").toSpanned()
        }

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

    override fun setAddress(address: String) {
        this.address.text = address
    }

    override fun setRegion(region: String) {
        this.regionName.text = region
    }

    override fun goToGoogleMaps(lat: Double, lng: Double) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng")))
    }

    override fun onDestroy() {
        super.onDestroy()
        androidLocationProvider.destroy()
    }

    fun onClickLocation(view: View) {
        presenter.navigateToGoogleMaps()
    }
}