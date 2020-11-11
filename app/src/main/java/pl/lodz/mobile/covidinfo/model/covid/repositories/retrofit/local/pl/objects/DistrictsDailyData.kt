package pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects

import com.google.gson.annotations.SerializedName


data class DistrictsDailyData (

	@SerializedName("dt") val date : String,
	@SerializedName("t") val totalCases : Int,
	@SerializedName("d") val totalDeaths : Int,
	// skipped field a
	@SerializedName("td") val todayCases : Int,
	@SerializedName("dd") val todayDeaths : Int,
	// skipped field ad
)