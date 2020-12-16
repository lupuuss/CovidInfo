package pl.lodz.mobile.covidinfo.modules.twitter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.widget.NestedScrollView
import kotlinx.android.synthetic.main.activity_twitter.*
import pl.lodz.mobile.covidinfo.R
import timber.log.Timber

class TwitterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)

        supportFragmentManager.beginTransaction()
            .add(R.id.twitterFragmentFrame, TwitterFragment.newInstance(TwitterFragment.Mode.InnerFullscreen))
            .commit()

        mainScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->

            if (!v.canScrollVertically(1)) {
                getTwitterFragment().loadMore()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.twitter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.refresh -> {
                getTwitterFragment().refresh()
            }
        }

        return true
    }

    private fun getTwitterFragment(): TwitterFragment {
        return supportFragmentManager.findFragmentById(R.id.twitterFragmentFrame) as TwitterFragment
    }
}