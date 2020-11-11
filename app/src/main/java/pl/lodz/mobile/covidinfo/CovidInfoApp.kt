package pl.lodz.mobile.covidinfo

import android.app.Application
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.model.covid.retrofit.global.Covid19Api
import pl.lodz.mobile.covidinfo.model.covid.retrofit.local.pl.CovidPlApi
import retrofit2.CallAdapter
import retrofit2.Converter
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
            androidLogger(Level.ERROR)

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

    single<Converter.Factory> { GsonConverterFactory.create(get(Gson::class.java)) }

    single<CallAdapter.Factory> { RxJava3CallAdapterFactory.create() }

    single { OkHttpClient() }

    single<Covid19Api> {
        Retrofit.Builder()
            .baseUrl(Covid19Api.url)
            .addConverterFactory(get(Converter.Factory::class.java))
            .addCallAdapterFactory(get(CallAdapter.Factory::class.java))
            .client(get(OkHttpClient::class.java))
            .build()
            .create(Covid19Api::class.java)
    }

    single<OkHttpClient>(named<CovidPlApi>()) {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Origin", CovidPlApi.siteUrl)
                    .build()
                chain.proceed(request)
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
}

private val androidModule = module { }