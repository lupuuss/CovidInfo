package pl.lodz.mobile.covidinfo.koin

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.CovidApi
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.CovidPlApi
import pl.lodz.mobile.covidinfo.model.twitter.TwitterApi
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object KoinRetrofitModule {

    fun build(): Module = module {

        single<Converter.Factory> { GsonConverterFactory.create(get(Gson::class.java)) }

        single<CallAdapter.Factory> { RxJava3CallAdapterFactory.create() }

        single { OkHttpClient() }

        single<CovidApi> {
            Retrofit.Builder()
                .baseUrl(CovidApi.url)
                .addConverterFactory(get(Converter.Factory::class.java))
                .addCallAdapterFactory(get(CallAdapter.Factory::class.java))
                .client(get(OkHttpClient::class.java))
                .build()
                .create(CovidApi::class.java)
        }

        single<OkHttpClient>(named<CovidPlApi>()) {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val oldRequest = chain.request()
                    val url = oldRequest.url()
                        .newBuilder()
                        .addQueryParameter("apiKey", get<Context>().getString(R.string.covid_pl_api_key))
                        .build()

                    val newRequest = oldRequest
                        .newBuilder()
                        .url(url)
                        .addHeader("Origin", CovidPlApi.siteUrl)
                        .build()

                    chain.proceed(newRequest)
                }.build()
        }

        single<CovidPlApi> {
            Retrofit.Builder()
                .baseUrl(CovidPlApi.url)
                .addConverterFactory(get(Converter.Factory::class.java))
                .addCallAdapterFactory(get(CallAdapter.Factory::class.java))
                .client(get(named<CovidPlApi>()))
                .build()
                .create(CovidPlApi::class.java)
        }

        single<OkHttpClient>(named<TwitterApi>()) {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val oldRequest = chain.request()
                    val url = oldRequest.url()
                        .newBuilder()
                        .build()

                    val newRequest = oldRequest
                        .newBuilder()
                        .url(url)
                        .addHeader("Authorization", get<Context>().getString(R.string.twitter_auth))
                        .build()

                    chain.proceed(newRequest)
                }.build()
        }

        single<TwitterApi> {
            Retrofit.Builder()
                .baseUrl(TwitterApi.url)
                .addConverterFactory(get(Converter.Factory::class.java))
                .addCallAdapterFactory(get(CallAdapter.Factory::class.java))
                .client(get(named<TwitterApi>()))
                .build()
                .create(TwitterApi::class.java)
        }
    }
}
