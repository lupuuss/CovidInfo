package pl.lodz.mobile.covidinfo.modules.summary

import pl.lodz.mobile.covidinfo.base.BasePresenterActions
import pl.lodz.mobile.covidinfo.base.BaseView

interface SummaryContract {

    sealed class Target : Comparable<Target> {

        object Global : Target() {
            override fun compareTo(other: Target): Int {
                return -1
            }

            override fun toString(): String {
                return "Target { Global }"
            }
        }

        class Country(val id: String) : Target() {

            override fun equals(other: Any?): Boolean {

                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Country

                if (id != other.id) return false

                return true
            }

            override fun hashCode(): Int {
                return id.hashCode()
            }

            override fun compareTo(other: Target): Int {

                return if (other is Country) {
                    this.id.compareTo(other.id)
                } else {
                    1
                }
            }

            override fun toString(): String {
                return "Target { id: $id }"
            }
        }
    }

    interface View : BaseView {
        fun setCases(total: String?, new: String? = null, isPositive: Boolean)
        fun setDeaths(total: String?, new: String? = null, isPositive: Boolean)
        fun setActive(total: String?, new: String? = null, isPositive: Boolean)
        fun setRecovered(total: String?, new: String? = null, isPositive: Boolean)
        fun setTargetsList(targets: List<String>)

        fun setSummaryName(name: String)

        var isPickTargetAvailable: Boolean
        var isLoading: Boolean
        var isContentVisible: Boolean
        var isContentLoadingError: Boolean
    }

    interface Presenter : BasePresenterActions<View> {

        fun refresh()
        fun pickTarget(position: Int)
    }
}