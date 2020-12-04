package pl.lodz.mobile.covidinfo.model.covid.data

data class Region(
        val id: String,
        val name: String,
        val level: Level
) {

    enum class Level {
        Global,
        Country,
        AdministrationLevel1,
        AdministrationLevel2
    }

    companion object {
        val global = Region("global", "Global", Level.Global)
    }
}