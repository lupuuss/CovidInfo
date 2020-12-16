package pl.lodz.mobile.covidinfo.model.covid.data

enum class CovidProperty {
    TotalCases,
    Cases,
    TotalDeaths,
    Deaths,
    TotalRecovered,
    Recovered,
    TotalActive,
    Active;

    fun extractFrom(data: CovidData): Int? {
        return when (this) {
            TotalCases -> data.totalCases
            Cases -> data.newCases
            TotalDeaths -> data.totalDeaths
            Deaths -> data.newDeaths
            TotalRecovered -> data.totalRecovered
            Recovered -> data.totalRecovered
            TotalActive -> data.totalActive
            Active -> data.newActive
        }
    }
}