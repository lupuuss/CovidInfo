package pl.lodz.mobile.covidinfo.model.covid.retrofit.local.pl.objects

data class DistrictsData (

    // skipped _id field
    // skipped gid field
    val name : String,
    val population : Int,
    // skipped geometry
    val dailyData : List<DistrictsDailyData>,
    val area : String,
    val provinceName : String,
    val districtName : String
)