package pl.lodz.mobile.covidinfo.koin

import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.model.covid.repositories.SupportedLocals
import pl.lodz.mobile.covidinfo.model.covid.repositories.local.PolandCovidRepository
import pl.lodz.mobile.covidinfo.modules.CovidTarget
import pl.lodz.mobile.covidinfo.modules.main.MainActivity
import pl.lodz.mobile.covidinfo.modules.main.MainContract
import pl.lodz.mobile.covidinfo.modules.main.MainPresenter
import pl.lodz.mobile.covidinfo.modules.plot.CountryPlotPresenter
import pl.lodz.mobile.covidinfo.modules.plot.PlotContract
import pl.lodz.mobile.covidinfo.modules.plot.PlotFragment
import pl.lodz.mobile.covidinfo.modules.plot.RegionalPlotPresenter
import pl.lodz.mobile.covidinfo.modules.ranking.RankingContract
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.ranking.GlobalRankingPresenter
import pl.lodz.mobile.covidinfo.modules.ranking.LocalRankingPresenter
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryPresenter
import pl.lodz.mobile.covidinfo.modules.twitter.TweetsPreviewFragment
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterActivity
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterContract
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterPresenter

object KoinAndroidModule {
    fun build() = module {

        scope<MainActivity> {
            scoped<MainContract.Presenter> { MainPresenter() }
        }

        scope<TwitterActivity> {
            scoped<TwitterContract.Presenter> {
                TwitterPresenter(
                    get(),
                    frontScheduler = KoinBaseModule.getFrontScheduler(this),
                    backScheduler = KoinBaseModule.getBackScheduler(this),
                    get()
                )
            }
        }

        scope<TweetsPreviewFragment> {
            scoped<TwitterContract.Presenter> {
                TwitterPresenter(
                        get(),
                        frontScheduler = KoinBaseModule.getFrontScheduler(this),
                        backScheduler = KoinBaseModule.getBackScheduler(this),
                        get()
                )
            }
        }

        scope<SummaryFragment> {
            scoped<SummaryContract.Presenter> { (target: CovidTarget, allowPickingTarget: Boolean) ->
                SummaryPresenter(
                    get(),
                    target,
                    frontScheduler = KoinBaseModule.getFrontScheduler(this),
                    backScheduler = KoinBaseModule.getBackScheduler(this),
                    allowPickingTarget
                )
            }
        }

        scope<RankingFragment> {
            scoped<RankingContract.Presenter> { (limit: Int, target: CovidTarget) ->
                if (target is CovidTarget.Global) {
                    GlobalRankingPresenter(
                        get(),
                        get(),
                        frontScheduler = KoinBaseModule.getFrontScheduler(this),
                        backScheduler = KoinBaseModule.getBackScheduler(this),
                        limit
                    )
                } else {
                    LocalRankingPresenter(
                            KoinRepositoryModule.getLocalCovidRepository(this, SupportedLocals.PL),
                            get(),
                            frontScheduler = KoinBaseModule.getFrontScheduler(this),
                            backScheduler = KoinBaseModule.getBackScheduler(this),
                            limit
                    )
                }
            }
        }

        scope<PlotFragment> {
            scoped<PlotContract.Presenter> { (limit: Int, target: CovidTarget) ->

                when (target) {
                    is CovidTarget.Country -> {
                        CountryPlotPresenter(
                            limit,
                            get(),
                            KoinBaseModule.getFrontScheduler(this),
                            KoinBaseModule.getBackScheduler(this),
                            get(),
                            target
                        )
                    }
                    is CovidTarget.RegionLevel1 -> {
                        RegionalPlotPresenter(
                            limit,
                            get(),
                            KoinBaseModule.getFrontScheduler(this),
                            KoinBaseModule.getBackScheduler(this),
                            KoinRepositoryModule.getLocalCovidRepository(this, SupportedLocals.PL),
                            target
                        )
                    }
                    else -> {
                        null!!
                    }
                }
            }
        }
    }

}
