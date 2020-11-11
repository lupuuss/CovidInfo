package pl.lodz.mobile.covidinfo

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.model.covid.data.CovidDaily
import pl.lodz.mobile.covidinfo.model.covid.data.CovidData
import pl.lodz.mobile.covidinfo.model.covid.data.Region
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.CovidApi
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.CovidPlApi
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryPresenter
import pl.lodz.mobile.covidinfo.modules.main.MainActivity
import pl.lodz.mobile.covidinfo.modules.main.MainContract
import pl.lodz.mobile.covidinfo.modules.main.MainPresenter
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.io.IOException
import java.util.concurrent.TimeUnit


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
                repositoryModule,
                androidModule
            )
        }
    }
}

private val baseModule = module {
    single { Gson() }
    single(named("frontScheduler")) { AndroidSchedulers.mainThread() }
    single(named("backScheduler")) { Schedulers.computation() }
}

private val retrofitModule = module {

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
}

private val repositoryModule = module {

}

private val androidModule = module {

    scope<MainActivity> {
        scoped<MainContract.Presenter> { MainPresenter() }
    }

    scope<SummaryFragment> {
        scoped<SummaryContract.Presenter> {
            SummaryPresenter(
                get(),
                SummaryContract.Target.Global,
                frontScheduler = get(named("frontScheduler")),
                backScheduler = get(named("backScheduler"))
            )
        }
    }
}