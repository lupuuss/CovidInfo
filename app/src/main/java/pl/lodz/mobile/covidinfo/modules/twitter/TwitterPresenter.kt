package pl.lodz.mobile.covidinfo.modules.twitter

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import pl.lodz.mobile.covidinfo.base.BasePresenter
import pl.lodz.mobile.covidinfo.model.twitter.TwitterApi
import pl.lodz.mobile.covidinfo.model.twitter.data.Tweet
import pl.lodz.mobile.covidinfo.model.twitter.data.TweetsData
import pl.lodz.mobile.covidinfo.model.twitter.data.User
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto

class TwitterPresenter(
        private val api: TwitterApi,
        private val frontScheduler: Scheduler,
        private val backScheduler: Scheduler,
        ) : BasePresenter<TwitterContract.View>(), TwitterContract.Presenter {

    private val userToDisplay = "MZ_GOV_PL"
    private val disposables = mutableListOf<Disposable>()
    private var user: User? = null

    private var nextToken: String? = null

    override fun refresh() {
        nextToken = null
        disposables.forEach(Disposable::dispose)

        val disposable = api.getUserByUserName(userToDisplay)
                .doOnSuccess(::handleUser)
                .flatMap {
                    api.getTweetsForUser(it.userName)
                }
                .subscribeOn(backScheduler)
                .observeOn(frontScheduler)
                .subscribe(this::handleTwitterResponse)

        disposables.add(disposable)
    }

    private fun handleUser(user: User?) {
        this.user = user
    }

    private fun handleTwitterResponse(data: TweetsData?, error: Throwable?) {

        if (data == null) {

            error?.printStackTrace()
            view?.isLoading = false
            view?.isContentLoadingError = true
            view?.isContentVisible = false
            return
        }

        val name = user?.let {
            it.name + "(@${it.userName})"
        } ?: userToDisplay

        val link = user?.profileImageUrl ?: ""

        val tweets = data.data.map {
            it.toTweetDto(name, link)
        }

        view?.addTweets(tweets)

        view?.isLoading = false
        view?.isContentLoadingError = false
        view?.isContentVisible = true
    }

    override fun loadMoreTweets() {

        nextToken?.let { nextToken ->

            val disposable = api.getNextTweetsForUser(userToDisplay, nextToken)
                    .subscribeOn(backScheduler)
                    .observeOn(frontScheduler)
                    .subscribe(this::handleTwitterResponse)

            disposables.add(disposable)

        } ?: return

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
                this.createAt.toString()
        )
    }
}