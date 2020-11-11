package pl.lodz.mobile.covidinfo.model.covid.data

data class Region(
        val id: String,
        val name: String,
        val level: Level
) {
    enum class Level {
        Country,
        AdministrationLevel1,
        AdministrationLevel2
    }
}