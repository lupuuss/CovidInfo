package pl.lodz.mobile.covidinfo.modules.twitter

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_twitter.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto
import pl.lodz.mobile.covidinfo.utility.decorations.BottomSpaceItemDecoration
import pl.lodz.mobile.covidinfo.utility.dpToPixels

class TwitterActivity : BaseActivity(), TwitterContract.View {
    private val presenter: TwitterContract.Presenter by currentScope.inject()

    private val tweets = mutableListOf<TweetDto>()

    override var isLoading: Boolean = false
        set(value) {
            field = value
            progressBar.isVisible = value
        }

    override var isContentVisible: Boolean = false
        set(value) {
            field = value
            recycler.isVisible = value
        }
    override var isContentLoadingError: Boolean = false
        set(value) {
            field = value
            errorMessage.isVisible = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)

        recycler.adapter = TweetsRecyclerAdapter(tweets, true)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(BottomSpaceItemDecoration(dpToPixels(this, 10f)))

        twitterScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->

            if (!v.canScrollVertically(1)) {
                presenter.loadMoreTweets()
            }
        })


        presenter.init(this)
    }

    override fun addTweets(tweets: List<TweetDto>) {
        val previousSize = this.tweets.size
        this.tweets.addAll(tweets)
        recycler.adapter?.notifyItemRangeInserted(previousSize, tweets.size)
    }

    override fun clearTweets() {
        this.tweets.clear()
        recycler.adapter?.notifyDataSetChanged()
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
}