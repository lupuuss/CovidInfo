package pl.lodz.mobile.covidinfo.modules.twitter

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView
import pl.lodz.mobile.covidinfo.base.DynamicContentView
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto

interface TwitterContract {

    interface View : DynamicContentView {
        fun addTweets(tweets: List<TweetDto>)
        fun clearTweets()
    }

    interface Presenter : BasePresenterActions<View> {
        fun refresh()
        fun loadMoreTweets()
    }
}