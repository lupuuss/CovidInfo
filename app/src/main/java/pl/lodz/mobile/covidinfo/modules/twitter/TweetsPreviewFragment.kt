package pl.lodz.mobile.covidinfo.modules.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_tweets_preview.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto
import pl.lodz.mobile.covidinfo.utility.dpToPixels

class TweetsPreviewFragment : BaseFragment(), TwitterContract.View {

    fun interface OnFragmentClick {
        fun onTweetsPreviewFragmentClick(fragment: TweetsPreviewFragment)
    }

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
            pager.isInvisible = !value
        }
    override var isContentLoadingError: Boolean = false
        set(value) {
            field = value
            errorMessage.isVisible = value
        }

    override fun addTweets(tweets: List<TweetDto>) {
        val previousSize = this.tweets.size
        this.tweets.addAll(tweets)
        pager.adapter?.notifyItemRangeInserted(previousSize, tweets.size)
    }

    override fun clearTweets() {
        this.tweets.clear()
        pager.adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tweets_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.init(this)

        if (activity is OnFragmentClick) {
            getView()?.setOnClickListener {
                (activity as OnFragmentClick).onTweetsPreviewFragmentClick(this)
            }
        }

        pager.adapter = TweetsRecyclerAdapter(tweets)
        pager.setPageTransformer(MarginPageTransformer(dpToPixels(requireContext(), 20f)))

        refreshButton.setOnClickListener { refresh() }

        TabLayoutMediator(tabsIndicator, pager) { _, _ -> }.attach()
    }

    private fun refresh() {
        presenter.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.close()
    }
}