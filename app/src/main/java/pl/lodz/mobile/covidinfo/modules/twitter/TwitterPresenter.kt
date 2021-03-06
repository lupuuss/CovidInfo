package pl.lodz.mobile.covidinfo.modules.twitter

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.model.twitter.TwitterApi
import pl.lodz.mobile.covidinfo.model.twitter.TwitterApi.Companion.getNextTweetsByUser
import pl.lodz.mobile.covidinfo.model.twitter.TwitterApi.Companion.getTweetsByUser
import pl.lodz.mobile.covidinfo.model.twitter.data.Tweet
import pl.lodz.mobile.covidinfo.model.twitter.data.TweetsData
import pl.lodz.mobile.covidinfo.model.twitter.data.User
import pl.lodz.mobile.covidinfo.model.twitter.data.UserResponse
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto
import pl.lodz.mobile.covidinfo.utility.date.DateFormatter

class TwitterPresenter(
        private val api: TwitterApi,
        private val frontScheduler: Scheduler,
        private val backScheduler: Scheduler,
        private val dateFormatter: DateFormatter
        ) : BasePresenter<TwitterContract.View>(), TwitterContract.Presenter {

    private val userToDisplay = "MZ_GOV_PL"

    private var moreAvailable = true

    private val disposables = mutableListOf<Disposable>()
    private var user: User? = null

    private var nextToken: String? = null

    override fun init(view: TwitterContract.View) {
        super.init(view)

        view.isContentVisible = true

        loadMoreTweets()
    }

    override fun close() {
        disposables.forEach(Disposable::dispose)
    }

    override fun refresh() {

        view?.clearTweets()

        view?.isLoading = true
        view?.isContentLoadingError = false

        moreAvailable = true
        nextToken = null

        disposables.forEach(Disposable::dispose)

        val disposable = api.getUserByUserName(userToDisplay)
                .doOnSuccess(::handleUser)
                .flatMap {
                    api.getTweetsByUser(userToDisplay)
                }
                .subscribeOn(backScheduler)
                .observeOn(frontScheduler)
                .subscribe(this::handleTwitterResponse)

        disposables.add(disposable)
    }

    override fun loadMoreTweets() {

        if (!moreAvailable) return

        view?.isLoading = true
        view?.isContentLoadingError = false

        nextToken?.let { nextToken ->

            val disposable = api.getNextTweetsByUser(userToDisplay, nextToken)
                    .subscribeOn(backScheduler)
                    .observeOn(frontScheduler)
                    .subscribe(this::handleTwitterResponse)

            disposables.add(disposable)
        }

        if (nextToken != null) return

        refresh()
    }

    private fun handleUser(user: UserResponse?) {
        this.user = user?.data
    }

    private fun handleTwitterResponse(data: TweetsData?, error: Throwable?) {

        if (data == null) {

            error?.printStackTrace()
            view?.isLoading = false
            view?.isContentLoadingError = true
            return
        }

        nextToken = data.meta.nextToken

        moreAvailable = nextToken != null

        val name = user?.name
            ?: userToDisplay

        val link = user?.profileImageUrl ?: ""

        val tweets = data.data.map {
            val imageLink = extractLink(data, it)
            it.toTweetDto(name, link, imageLink)
        }

        view?.isLoading = false
        view?.isContentLoadingError = false

        view?.addTweets(tweets)
    }

    private fun extractLink(data: TweetsData, it: Tweet): String? {

        if (it.attachments?.mediaKeys?.isEmpty() == true) return null

        val key = it.attachments?.mediaKeys?.first() ?: return null

        val media = data.includes.media.find { it.mediaKey == key } ?: return null

        if (media.type != "photo") return null

        return media.url
    }

    private fun Tweet.toTweetDto(user: String, imageUrl: String, tweetImageUrl: String?): TweetDto {

        return TweetDto(
                user,
                imageUrl,
                this.text,
                dateFormatter.getRelativeDateStringFromIsoDate(this.createAt),
                tweetImageUrl
        )
    }
}