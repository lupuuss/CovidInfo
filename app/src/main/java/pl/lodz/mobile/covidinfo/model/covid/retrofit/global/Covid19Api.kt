package pl.lodz.mobile.covidinfo.model.covid.retrofit.global

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.retrofit.global.objects.Country
import pl.lodz.mobile.covidinfo.model.covid.retrofit.global.objects.CountryDaily
import pl.lodz.mobile.covidinfo.model.covid.retrofit.global.objects.Summary

import retrofit2.http.GET
import retrofit2.http.Path

interface Covid19Api {

    companion object {
        const val url = "https://api.covid19api.com/"
    }

    @GET("countries")
    fun getCountries(): Single<List<Country>>

    @GET("summary")
    fun getSummary(): Single<Summary>

    @GET("dayone/country/{slug}")
    fun getDailyByCountry(@Path("slug")countrySlug: String): Single<List<CountryDaily>>
}