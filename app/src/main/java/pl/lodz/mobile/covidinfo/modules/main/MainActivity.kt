package pl.lodz.mobile.covidinfo.modules.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.horizontal_scroll_card.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterActivity
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterFragment
import pl.lodz.mobile.covidinfo.modules.world.WorldActivity
import pl.lodz.mobile.covidinfo.utility.dpToPixels

class MainActivity : BaseActivity(), MainContract.View, TwitterFragment.OnFragmentClick {

    private val presenter: MainContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fillMainContainer(savedInstanceState == null)

        presenter.init(this)
    }

    private fun fillMainContainer(initFragments: Boolean) {

        val card = getCardWithTitle(R.string.world)

        val pager = card.findViewById<ViewPager2>(R.id.pager)

        pager.setPageTransformer(MarginPageTransformer(dpToPixels(this, 20)))

        val indicator = card.findViewById<TabLayout>(R.id.tabsIndicator)

        pager.adapter = WorldFragmentsAdapter(this)

        TabLayoutMediator(indicator, pager) { _, _ -> }.attach()

        card.setOnClickListener { onClickWorldCard() }

        mainScrollContainer.addView(card)

        if (initFragments) {

            supportFragmentManager.beginTransaction()
                    .add(mainScrollContainer.id, TwitterFragment.newInstance(TwitterFragment.Mode.Widget))
                    .commit()
        }
    }

    private fun onClickWorldCard() {
        presenter.goToWorld()
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

    override fun navigateToWorld() {
        startActivity(Intent(this, WorldActivity::class.java))
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