package pl.lodz.mobile.covidinfo

import android.app.Application
import com.github.mikephil.charting.BuildConfig
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.model.covid.retrofit.local.pl.CovidPlApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.DebugTree


class CovidInfoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        startKoin {

            androidContext(this@CovidInfoApp)
            androidLogger()

            modules(
                baseModule,
                retrofitModule,
                androidModule
            )
        }
    }
}

private val baseModule = module {
    single { Gson() }
}

private val retrofitModule = module {

    single<CovidPlApi> {

        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Origin", CovidPlApi.siteUrl)
                    .build()
                chain.proceed(request)
            }.build()

        Retrofit.Builder()
            .baseUrl(CovidPlApi.url)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(CovidPlApi::class.java)
    }
}

private val androidModule = module { }