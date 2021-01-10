package pl.lodz.mobile.covidinfo.koin

import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.lodz.mobile.covidinfo.modules.main.MainActivity
import pl.lodz.mobile.covidinfo.modules.main.MainContract
import pl.lodz.mobile.covidinfo.modules.main.MainPresenter
import pl.lodz.mobile.covidinfo.modules.ranking.RankingContract
import pl.lodz.mobile.covidinfo.modules.ranking.RankingFragment
import pl.lodz.mobile.covidinfo.modules.ranking.RankingPresenter
import pl.lodz.mobile.covidinfo.modules.summary.SummaryContract
import pl.lodz.mobile.covidinfo.modules.summary.SummaryFragment
import pl.lodz.mobile.covidinfo.modules.summary.SummaryPresenter
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterContract
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterFragment
import pl.lodz.mobile.covidinfo.modules.twitter.TwitterPresenter

object KoinAndroidModule {
    fun build() = module {

        scope<MainActivity> {
            scoped<MainContract.Presenter> { MainPresenter() }
        }

        scope<TwitterFragment> {
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
            scoped<SummaryContract.Presenter> { (target: SummaryContract.Target, allowPickingTarget: Boolean) ->
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
            scoped<RankingContract.Presenter> { (limit: Int) ->
                RankingPresenter(
                    get(),
                    get(),
                    frontScheduler = KoinBaseModule.getFrontScheduler(this),
                    backScheduler = KoinBaseModule.getBackScheduler(this),
                    limit
                )
            }
        }
    }

}
