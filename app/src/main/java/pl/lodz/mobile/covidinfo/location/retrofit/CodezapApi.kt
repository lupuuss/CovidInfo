package pl.lodz.mobile.covidinfo.location.retrofit

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CodezapApi {

    companion object {
        const val url = "https://api.codezap.io/v1/"
    }

    @GET("reverse")
    fun reverse(
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("language") language: String = "en"
    ): Single<GeoCoding>
}