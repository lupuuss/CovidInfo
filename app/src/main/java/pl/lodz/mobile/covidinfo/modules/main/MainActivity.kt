package pl.lodz.mobile.covidinfo.modules.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterActivity
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterFragment

class MainActivity : BaseActivity(), MainContract.View, TwitterFragment.OnFragmentClick {

    private val presenter: MainContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fillMainContainer()

        presenter.init(this)
    }

    private fun fillMainContainer() {
        val worldCard = getCardWithTitle(R.string.world)
        val container = worldCard.findViewById<LinearLayout>(R.id.container)

        container.id = View.generateViewId()

        mainScrollContainer.addView(worldCard)

        supportFragmentManager.beginTransaction()
            .add(container.id, SummaryFragment.getInstance(allowPickingTarget = true))
            .commit()

        supportFragmentManager.beginTransaction()
            .add(mainScrollContainer.id, TwitterFragment.newInstance(TwitterFragment.Mode.Widget))
            .commit()
    }

    private fun getCardWithTitle(@StringRes title: Int): View {
        val horizontalCard = layoutInflater.inflate(
            R.layout.horizontal_scroll_card,
            mainScrollContainer,
            false
        )!!

        horizontalCard
            .findViewById<TextView>(R.id.title)
            .setText(title)

        return horizontalCard
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            presenter.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings -> presenter.goToSettings()
        }

        return true
    }

    override fun navigateToSettings() {
        TODO("Not yet implemented")
    }

    override fun navigateToCovidInYourArea() {
        TODO("Not yet implemented")
    }

    override fun navigateToTwitter() {
        startActivity(Intent(this, TwitterActivity::class.java))
    }

    // onClicks

    @Suppress("UNUSED_PARAMETER")
    fun onClickCovidInYourArea(view: View) {
        presenter.goToCovidInYourArea()
    }

    override fun onTwitterFragmentClick(fragment: TwitterFragment) {
        presenter.goToTwitter()
    }
}