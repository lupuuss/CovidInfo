package pl.lodz.mobile.covidinfo.koin

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

object KoinMain {
    fun start(androidContext: Context) {
        startKoin {

            androidContext(androidContext)
            androidLogger(Level.ERROR)

            modules(
                baseModule,
                retrofitModule,
                repositoryModule,
                androidModule
            )
        }
    }

    private val baseModule: Module by lazy { KoinBaseModule.build() }
    private val retrofitModule: Module by lazy { KoinRetrofitModule.build() }
    private val repositoryModule: Module by lazy { KoinRepositoryModule.build() }
    private val androidModule: Module by lazy { KoinAndroidModule.build() }
}