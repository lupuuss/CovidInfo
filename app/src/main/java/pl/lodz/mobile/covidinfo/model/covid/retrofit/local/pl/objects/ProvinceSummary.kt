package pl.lodz.mobile.covidinfo.model.covid.retrofit.local.pl.objects

import com.google.gson.annotations.SerializedName

data class ProvinceSummary (
	@SerializedName("_id") val name: String,
	val confirmed : Int,
	val deaths : Int,
	@SerializedName("time_stamp") val timestamp : String
)