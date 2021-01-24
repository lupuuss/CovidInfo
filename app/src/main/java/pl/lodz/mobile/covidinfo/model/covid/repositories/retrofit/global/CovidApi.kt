package pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.objects.Country
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.objects.CountryDaily
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.objects.Summary

import retrofit2.http.GET
import retrofit2.http.Path

interface CovidApi {

    companion object {
        const val url = "https://api.covid19api.com/"
    }

    @GET("countries")
    fun getCountries(): Single<List<Country>>

    @GET("summary")
    fun getSummary(): Single<Summary>

    @GET("/total/dayone/country/{slug}")
    fun getDailyByCountry(@Path("slug")countrySlug: String): Single<List<CountryDaily>>
}