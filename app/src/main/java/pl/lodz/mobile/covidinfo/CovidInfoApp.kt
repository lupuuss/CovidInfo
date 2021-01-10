package pl.lodz.mobile.covidinfo

import android.app.Application
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import pl.lodz.mobile.covidinfo.koin.KoinMain
import retrofit2.HttpException
import timber.log.Timber
import timber.log.Timber.DebugTree


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