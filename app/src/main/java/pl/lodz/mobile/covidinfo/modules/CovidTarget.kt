package pl.lodz.mobile.covidinfo.modules

sealed class CovidTarget : Comparable<CovidTarget> {

    object Global : CovidTarget() {
        override fun compareTo(other: CovidTarget): Int {
            return -1
        }

        override fun toString(): String {
            return "global"
        }
    }

    class Country(val id: String) : CovidTarget() {

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

        override fun compareTo(other: CovidTarget): Int {

            return if (other is Country) {
                this.id.compareTo(other.id)
            } else {
                1
            }
        }

        override fun toString(): String {
            return id
        }
    }
}
