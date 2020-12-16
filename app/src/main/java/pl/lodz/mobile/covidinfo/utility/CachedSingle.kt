package pl.lodz.mobile.covidinfo.utility

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class CachedSingle<T>(
    private val timeProvider: () -> Long,
    time: Long,
    unit: TimeUnit,
    private val provider: () -> Single<T>,
) {

    private var lastRealGet = 0L
    private var cache: T? = null
    private val cacheSubject: Subject<T> = PublishSubject.create()
    private val timeoutInMs = TimeUnit.MILLISECONDS.convert(time, unit)
    private var queryInProgress: AtomicBoolean = AtomicBoolean(false)

    val observable: Single<T> = Single.fromCallable {

        val currentTime = timeProvider()

        if (cache != null && currentTime - lastRealGet < timeoutInMs) {
            Timber.d("$this returned cached data!")
            return@fromCallable cache!!
        }

        if (queryInProgress.get()) {
            Timber.d("$this waits in queue...")
            return@fromCallable cacheSubject.blockingFirst().also {
                Timber.d("$this out of queue!")
            }
        }

        queryInProgress.set(true)

        try {
            cache = provider().blockingGet()
        } catch (e: Exception) {
            Timber.d("$this provider threw an exception: $e")
            cacheSubject.onError(e)
            throw e
        } finally {
            queryInProgress.set(false)
        }

        lastRealGet = currentTime

        cacheSubject.onNext(cache)

        Timber.d("$this provided new data!")

        return@fromCallable cache!!
    }
}