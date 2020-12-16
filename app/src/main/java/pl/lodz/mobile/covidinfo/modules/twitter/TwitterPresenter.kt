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
import timber.log.Timber

class TwitterPresenter(
        private val api: TwitterApi,
        private val frontScheduler: Scheduler,
        private val backScheduler: Scheduler,
        private val dateFormatter: DateFormatter
        ) : BasePresenter<TwitterContract.View>(), TwitterContract.Presenter {

    private val userToDisplay = "MZ_GOV_PL"
    private val disposables = mutableListOf<Disposable>()
    private var user: User? = null

    private var nextToken: String? = null

    override fun init(view: TwitterContract.View) {
        super.init(view)

        loadMoreTweets()
    }

    override fun refresh() {

        view?.clearTweets()

        view?.isLoading = true
        view?.isContentLoadingError = false
        view?.isContentVisible = false

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

    private fun handleUser(user: UserResponse?) {
        this.user = user?.data
    }

    private fun handleTwitterResponse(data: TweetsData?, error: Throwable?) {

        if (data == null) {

            error?.printStackTrace()
            view?.isLoading = false
            view?.isContentLoadingError = true
            view?.isContentVisible = false
            return
        }

        nextToken = data.meta.nextToken

        val name = user?.let { it.name + "(@${it.userName})" }
            ?: userToDisplay

        val link = user?.profileImageUrl ?: ""

        val tweets = data.data.map {
            it.toTweetDto(name, link)
        }

        view?.isLoading = false
        view?.isContentLoadingError = false
        view?.isContentVisible = true

        view?.addTweets(tweets)
    }

    override fun loadMoreTweets() {

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

    override fun close() {
        disposables.forEach(Disposable::dispose)
    }

    private fun Tweet.toTweetDto(user: String, imageUrl: String): TweetDto {

        return TweetDto(
            user,
            imageUrl,
            this.text,
            dateFormatter.getRelativeDateStringFromIsoDate(this.createAt)
        )
    }
}