package pl.lodz.mobile.covidinfo.modules.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.AppTheme
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.modules.area.YourAreaActivity
import pl.lodz.mobile.covidinfo.modules.poland.PolandActivity
import pl.lodz.mobile.covidinfo.modules.twitter.TweetsPreviewFragment
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterActivity
import pl.lodz.mobile.covidinfo.modules.world.WorldActivity
import timber.log.Timber

class MainActivity : BaseActivity(), MainContract.View, TweetsPreviewFragment.OnFragmentClick, CardFragment.OnClick{

    private val locationPermissionRequestCode: Int = 1337

    private val presenter: MainContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainScrollView.isNestedScrollingEnabled = false

        if (savedInstanceState == null) {
            addWorldModule()
            addPolandModule()
            addTwitterModule()
        }

        presenter.init(this)
    }

    private fun addWorldModule() {
        supportFragmentManager.beginTransaction()
                .add(mainScrollContainer.id, CardFragment.newInstance(CardFragment.Pager.World))
                .commit()
    }

    private fun addPolandModule() {
        supportFragmentManager.beginTransaction()
                .add(mainScrollContainer.id, CardFragment.newInstance(CardFragment.Pager.Poland))
                .commit()
    }

    private fun addTwitterModule() {
        supportFragmentManager.beginTransaction()
                .add(mainScrollContainer.id, TweetsPreviewFragment())
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        menu?.findItem(R.id.switch_theme_item)?.let {

            it.setActionView(R.layout.theme_switch_layout)
            val switch = it.actionView?.findViewById<SwitchCompat>(R.id.theme_switch)!!
            switch.isChecked = AppTheme.get(this) == AppTheme.Name.Dark

            switch.setOnCheckedChangeListener { _, isChecked ->
                swapTheme(isChecked)
            }
        }


        return true
    }

    private fun swapTheme(activated: Boolean) {
        if (activated) {
            AppTheme.set(this, AppTheme.Name.Dark)
        } else {
            AppTheme.set(this, AppTheme.Name.Light)
        }

        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()

        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            presenter.close()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != locationPermissionRequestCode) return

        if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            onLocationPermissionGranted()
            return
        }

        onLocationPermissionDenied()
    }

    private fun onLocationPermissionDenied() {
        showQuickDialog(getString(R.string.no_location_permission))
    }

    private fun onLocationPermissionGranted() {
        startActivity(Intent(this, YourAreaActivity::class.java))
    }

    override fun navigateToCovidInYourArea() {

        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (result != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )

            return
        }

        onLocationPermissionGranted()
    }

    override fun navigateToTwitter() {
        startActivity(Intent(this, TwitterActivity::class.java))
    }

    override fun navigateToWorld() {
        startActivity(Intent(this, WorldActivity::class.java))
    }

    override fun navigateToPoland() {
        startActivity(Intent(this, PolandActivity::class.java))
    }

    // onClicks

    @Suppress("UNUSED_PARAMETER")
    fun onClickCovidInYourArea(view: View) {
        presenter.goToCovidInYourArea()
    }

    override fun onTweetsPreviewFragmentClick(fragment: TweetsPreviewFragment) {
        presenter.goToTwitter()
    }

    override fun onClickCardFragment(pager: CardFragment.Pager) {
        when (pager) {
            CardFragment.Pager.World -> presenter.goToWorld()
            CardFragment.Pager.Poland -> presenter.goToPoland()
        }
    }
}