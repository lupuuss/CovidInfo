package pl.lodz.mobile.covidinfo

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.koin.KoinMain
import pl.lodz.mobile.covidinfo.localization.AndroidResourcesManager
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.model.covid.repositories.BasicCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.local.PolandCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.CovidApi
import pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.CovidPlApi
import pl.lodz.mobile.covidinfo.model.twitter.TwitterApi
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryPresenter
import pl.lodz.mobile.covidinfo.modules.main.MainActivity
import pl.lodz.mobile.covidinfo.modules.main.MainContract
import pl.lodz.mobile.covidinfo.modules.main.MainPresenter
import pl.lodz.mobile.covidinfo.modules.ranking.RankingContract
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.ranking.RankingPresenter
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterContract
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterFragment
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterPresenter
import pl.lodz.mobile.covidinfo.utility.date.AndroidDateFormatter
import pl.lodz.mobile.covidinfo.utility.date.DateFormatter
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

        RxJavaPlugins.setErrorHandler {
            Timber.d("Unhandled exception! $it")
        }

        KoinMain.start(this)
    }
}