package pl.lodz.mobile.covidinfo.modules.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_twitter.*
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto
import pl.lodz.mobile.covidinfo.utility.decorations.BottomSpaceItemDecoration
import pl.lodz.mobile.covidinfo.utility.decorations.LeftRightSpaceItemDecoration
import pl.lodz.mobile.covidinfo.utility.dpToPixels
import timber.log.Timber


class TwitterFragment : BaseFragment(), TwitterContract.View {

    enum class Orientation(val linearLayoutOrientation: Int) {
        Vertical(LinearLayoutManager.VERTICAL), Horizontal(LinearLayoutManager.HORIZONTAL)
    }

    private lateinit var orientation: Orientation

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
        val previousSize = tweets.size
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
            orientation = Orientation.valueOf(it.getString(orientationBundle)!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_twitter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.adapter = TweetsRecyclerAdapter(tweets, orientation)
        recycler.isNestedScrollingEnabled = orientation == Orientation.Horizontal
        recycler.layoutManager =
            LinearLayoutManager(this.context, orientation.linearLayoutOrientation, false)

        val decoration = when (orientation) {
            Orientation.Vertical -> BottomSpaceItemDecoration(dpToPixels(requireContext(), 10))
            Orientation.Horizontal -> LeftRightSpaceItemDecoration(dpToPixels(requireContext(), 10))
        }

        recycler.addItemDecoration(decoration)

        refreshButton.setOnClickListener {
            Timber.d("XDDXD")
            presenter.refresh() }

        presenter.init(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.close()
    }

    companion object {

        private const val orientationBundle = "TwitterFragmentOrientationBundle"

        fun newInstance(orientation: Orientation): TwitterFragment {
            val args = Bundle()

            args.putString(orientationBundle, orientation.name)

            return TwitterFragment().apply { arguments = args }
        }
    }
}