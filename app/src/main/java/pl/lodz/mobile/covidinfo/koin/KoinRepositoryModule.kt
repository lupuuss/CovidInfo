package pl.lodz.mobile.covidinfo.koin

import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.model.covid.repositories.BasicCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.CovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.LocalCovidRepository
import pl.lodz.mobile.covidinfo.model.covid.repositories.SupportedLocals
import pl.lodz.mobile.covidinfo.model.covid.repositories.local.PolandCovidRepository

object KoinRepositoryModule {

    fun build() = module {

        single<LocalCovidRepository>(named(SupportedLocals.PL)) {
            PolandCovidRepository(
                get(),
                KoinBaseModule.getTimeProvider(this),
                get()
            )
        }

        single<CovidRepository> {

            val supportedLocals = mapOf(
                SupportedLocals.PL to get<LocalCovidRepository>(named(SupportedLocals.PL))
            )

            BasicCovidRepository(
                get(),
                get(),
                supportedLocals,
                KoinBaseModule.getTimeProvider(this)
            )
        }
    }

    fun getLocalCovidRepository(scope: Scope, supportedLocals: SupportedLocals): LocalCovidRepository {
        return scope.get(named(supportedLocals))
    }
}
