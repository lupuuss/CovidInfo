package pl.lodz.mobile.covidinfo.modules

class CovidPropertyDto(
    val name: Name,
    val localizedName: String
) {

    enum class Name {
        TotalCases, Cases, TotalDeaths, Deaths, TotalRecovered, Recovered, TotalActive, Active
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CovidPropertyDto

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}