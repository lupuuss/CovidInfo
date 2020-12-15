package pl.lodz.mobile.covidinfo.modules.twitter

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto

interface TwitterContract {

    interface View : BaseView {

        var isLoading: Boolean
        var isContentVisible: Boolean
        var isContentLoadingError: Boolean

        fun addTweets(tweets: List<TweetDto>)
    }

    interface Presenter : BasePresenterActions<View> {
        fun refresh()
        fun loadMoreTweets()
    }
}