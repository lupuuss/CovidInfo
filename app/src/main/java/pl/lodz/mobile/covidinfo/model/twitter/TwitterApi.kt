package pl.lodz.mobile.covidinfo.model.twitter

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.twitter.data.TweetsData
import pl.lodz.mobile.covidinfo.model.twitter.data.User
import retrofit2.http.GET
import retrofit2.http.Path

interface TwitterApi {

    companion object {
        const val url = "https://api.twitter.com/2/"
    }

    @GET("tweets/search/recent?tweet.fields=id,text,in_reply_to_user_id,created_at&query=from:{user}")
    fun getTweetsForUser(@Path("user") user: String): Single<TweetsData>

    @GET("tweets/search/recent?tweet.fields=id,text,in_reply_to_user_id,created_at&query=from:{user}&next_token={nextToken}")
    fun getNextTweetsForUser(@Path("user") user: String, @Path("nextToken") nextToken: String): Single<TweetsData>

    @GET("users/by/username/{user}?user.fields=profile_image_url")
    fun getUserByUserName(@Path("user") user: String): Single<User>
}