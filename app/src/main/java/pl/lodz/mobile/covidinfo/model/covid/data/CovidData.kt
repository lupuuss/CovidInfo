package pl.lodz.mobile.covidinfo.model.covid.data

data class CovidData (
    val totalCases: Int? = null,
    val newCases: Int? = null,
    val totalDeaths: Int? = null,
    val newDeaths: Int? = null,
    val totalRecovered: Int? = null,
    val newRecovered: Int? = null,
    val totalActive: Int? = null,
    val newActive: Int? = null
) {
    constructor(
        totalCases: Int,
        newCases: Int,
        totalDeaths: Int,
        newDeaths: Int,
        totalRecovered: Int,
        newRecovered: Int
    ) : this(
        totalCases,
        newCases,
        totalDeaths,
        newDeaths,
        totalRecovered,
        newRecovered,
        totalCases - totalDeaths - totalRecovered,
        newCases - newDeaths - newRecovered
    )
}