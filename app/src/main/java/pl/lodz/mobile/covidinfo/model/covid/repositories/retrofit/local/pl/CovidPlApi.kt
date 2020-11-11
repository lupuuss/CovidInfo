package pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl

import io.reactivex.rxjava3.core.Single
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects.DistrictsData
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects.ProvinceSummary
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects.ProvincesDailyDetails
import retrofit2.http.GET

/**
 * Please don't judge me for this shitty API. I have no alternative :(.
 */
interface CovidPlApi {

    companion object {
        const val url = "https://api-korona-wirus.pl"
        const val siteUrl = "https://koronawirus-w-polsce.pl"
    }

    @GET("districtsOx")
    fun getDailyDistricts(): Single<List<DistrictsData>>

    @GET("provinces")
    fun getProvinces(): Single<List<ProvinceSummary>>

    @GET("provincesdeths")
    fun getDailyProvincesDeaths(): Single<List<ProvincesDailyDetails>>

    @GET("provincesconfirmed")
    fun getDailyProvincesCases(): Single<List<ProvincesDailyDetails>>

    @GET("provincesrecovered")
    fun getDailyProvincesRecovered(): Single<List<ProvincesDailyDetails>>

}