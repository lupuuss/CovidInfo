package pl.lodz.mobile.covidinfo

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.model.covid.repositories.BasicCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
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
import java.util.*


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
    single(named("timeProvider")) { { System.currentTimeMillis() } }

    single<Locale> {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            androidContext().resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            androidContext().resources.configuration.locale
        }
    }
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
    single<CovidRepository> {
        BasicCovidRepository(get(), get(), emptyMap(), get(named("timeProvider")))
    }
}

private val androidModule = module {

    scope<MainActivity> {
        scoped<MainContract.Presenter> { MainPresenter() }
    }

    scope<SummaryFragment> {
        scoped<SummaryContract.Presenter> { (target: SummaryContract.Target) ->
            SummaryPresenter(
                get(),
                target,
                frontScheduler = get(named("frontScheduler")),
                backScheduler = get(named("backScheduler"))
            )
        }
    }
}