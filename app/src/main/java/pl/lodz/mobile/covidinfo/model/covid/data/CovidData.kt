package pl.lodz.mobile.covidinfo.model.covid.data

data class CovidData (
    val totalCases: Int?,
    val newCases: Int?,
    val totalDeaths: Int?,
    val newDeaths: Int?,
    val totalRecovered: Int?,
    val newRecovered: Int?,
    val totalActive: Int?,
    val newActive: Int?
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