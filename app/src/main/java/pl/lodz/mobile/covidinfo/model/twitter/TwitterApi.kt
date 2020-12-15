package pl.lodz.mobile.covidinfo.model.twitter

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.twitter.data.TweetsData
import pl.lodz.mobile.covidinfo.model.twitter.data.User
import pl.lodz.mobile.covidinfo.model.twitter.data.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitterApi {


    @GET("tweets/search/recent?tweet.fields=id,text,in_reply_to_user_id,created_at")
    fun getTweetsByQuery(@Query("query") query: String): Single<TweetsData>

    @GET("tweets/search/recent?tweet.fields=id,text,in_reply_to_user_id,created_at")
    fun getNextTweetsByQuery(@Query("query") query: String, @Query("next_token") nextToken: String): Single<TweetsData>

    @GET("users/by/username/{user}?user.fields=profile_image_url")
    fun getUserByUserName(@Path("user") user: String): Single<UserResponse>

    companion object {
        const val url = "https://api.twitter.com/2/"

        fun TwitterApi.getTweetsByUser(user: String): Single<TweetsData> {
            return getTweetsByQuery("from:$user")
        }

        fun TwitterApi.getNextTweetsByUser(user: String, nextToken: String): Single<TweetsData> {
            return getNextTweetsByQuery("from:$user", nextToken)
        }
    }
}