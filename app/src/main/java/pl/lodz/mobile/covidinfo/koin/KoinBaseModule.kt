package pl.lodz.mobile.covidinfo.koin

import android.os.Build
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.localization.AndroidResourcesManager
import pl.lodz.mobile.covidinfo.localization.ResourcesManager
import pl.lodz.mobile.covidinfo.location.AndroidLocationProvider
import pl.lodz.mobile.covidinfo.location.LocationProvider
import pl.lodz.mobile.covidinfo.utility.date.AndroidDateFormatter
import pl.lodz.mobile.covidinfo.utility.date.DateFormatter
import java.util.*

object KoinBaseModule {

    fun build(): Module = module {

        single { Gson() }
        single(named("frontScheduler")) { AndroidSchedulers.mainThread() }
        single(named("backScheduler")) { Schedulers.computation() }
        single(named("timeProvider")) { { System.currentTimeMillis() } }

        single<Locale> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                androidContext().resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                androidContext().resources.configuration.locale
            }
        }

        single<DateFormatter> { AndroidDateFormatter(get(named("timeProvider")), get()) }

        single<ResourcesManager> { AndroidResourcesManager(androidContext()) }

        single<LocationProvider> {
            AndroidLocationProvider()
        }
    }

    fun getFrontScheduler(scope: Scope): Scheduler = scope.get(named("frontScheduler"))

    fun getBackScheduler(scope: Scope): Scheduler = scope.get(named("backScheduler"))

    fun getTimeProvider(scope: Scope): () -> Long = scope.get(named("timeProvider"))
}