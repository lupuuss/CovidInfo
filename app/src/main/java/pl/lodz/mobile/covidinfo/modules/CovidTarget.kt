package pl.lodz.mobile.covidinfo.modules

sealed class CovidTarget : Comparable<CovidTarget> {

    abstract val id: String

    abstract val name: String

    object Global : CovidTarget() {
        override val id: String
            get() = "global"
        override val name: String
            get() = "global"

        override fun compareTo(other: CovidTarget): Int {
            return -1
        }

        override fun toString(): String {
            return "global"
        }
    }

    open class IdTarget(override val id: String, override val name: String) : CovidTarget() {

        override fun equals(other: Any?): Boolean {

            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as IdTarget

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun compareTo(other: CovidTarget): Int {

            return if (other is IdTarget) {
                this.id.compareTo(other.id)
            } else {
                1
            }
        }

        override fun toString(): String {
            return id
        }
    }

    open class Country(id: String, name: String) : IdTarget(id, name) {

        object Poland : Country("poland", "Poland")
        object Germany : Country("germany", "Germany")
        object Spain : Country("spain", "Spain")
    }

    open class RegionLevel1(id: String, val country: Country, name: String) : IdTarget(id, name) {
        object Mazowieckie : RegionLevel1("mazowieckie", Country.Poland, "Mazowieckie")
    }
}
