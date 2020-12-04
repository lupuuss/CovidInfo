package pl.lodz.mobile.covidinfo.utility

import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CachedSingle<T>(
    private val timeProvider: () -> Long,
    time: Long,
    unit: TimeUnit,
    private val provider: () -> Single<T>,
) {

    private var lastRealGet = 0L
    private var cache: T? = null
    private val timeoutInMs = TimeUnit.MILLISECONDS.convert(time, unit)

    val observable = Single.fromCallable {

        val currentTime = timeProvider()

        if (cache != null && currentTime - lastRealGet < timeoutInMs) return@fromCallable cache!!

        cache = provider().blockingGet()

        lastRealGet = currentTime

        return@fromCallable cache!!
    }
}