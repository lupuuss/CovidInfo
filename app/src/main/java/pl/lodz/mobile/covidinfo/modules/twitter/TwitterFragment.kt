package pl.lodz.mobile.covidinfo.modules.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_twitter.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto
import pl.lodz.mobile.covidinfo.utility.decorations.BottomSpaceItemDecoration
import pl.lodz.mobile.covidinfo.utility.decorations.LeftRightSpaceItemDecoration
import pl.lodz.mobile.covidinfo.utility.dpToPixels


class TwitterFragment : BaseFragment(), TwitterContract.View {

    fun interface OnFragmentClick {
        fun onTwitterFragmentClick(fragment: TwitterFragment)
    }

    enum class Mode(val linearLayoutOrientation: Int) {
        InnerFullscreen(LinearLayoutManager.VERTICAL), Widget(LinearLayoutManager.HORIZONTAL)
    }

    private lateinit var mode: Mode

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

    override fun addTweets(tweets: List<TweetDto>) {
        val previousSize = this.tweets.size
        this.tweets.addAll(tweets)
        recycler.adapter?.notifyItemRangeInserted(previousSize, tweets.size)
    }

    override fun clearTweets() {
        this.tweets.addAll(tweets)
        recycler.adapter?.notifyItemRangeRemoved(0, tweets.size)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mode = Mode.valueOf(it.getString(orientationBundle)!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_twitter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val decoration: RecyclerView.ItemDecoration

        when (mode) {
            Mode.InnerFullscreen -> {
                getView()?.background?.alpha = 0
                refreshButton.isGone = true
                title.isGone = true
                decoration = BottomSpaceItemDecoration(dpToPixels(requireContext(), 10))
            }
            Mode.Widget -> {

                decoration = LeftRightSpaceItemDecoration(dpToPixels(requireContext(), 10))
            }
        }


        recycler.adapter = TweetsRecyclerAdapter(tweets, mode)
        recycler.isNestedScrollingEnabled = mode == Mode.Widget
        recycler.layoutManager =
            LinearLayoutManager(this.context, mode.linearLayoutOrientation, false)

        recycler.addItemDecoration(decoration)

        refreshButton.setOnClickListener {
            refresh()
        }

        if (activity is OnFragmentClick) {
            getView()?.setOnClickListener {
                (activity as OnFragmentClick).onTwitterFragmentClick(this)
            }
        }

        presenter.init(this)
    }

    fun refresh() {
        presenter.refresh()
    }

    fun loadMore() {
        presenter.loadMoreTweets()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.close()
    }

    companion object {

        private const val orientationBundle = "TwitterFragmentOrientationBundle"

        fun newInstance(mode: Mode): TwitterFragment {
            val args = Bundle()

            args.putString(orientationBundle, mode.name)

            return TwitterFragment().apply { arguments = args }
        }
    }
}