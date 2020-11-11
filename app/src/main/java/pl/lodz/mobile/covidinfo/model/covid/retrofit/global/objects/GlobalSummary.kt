package pl.lodz.mobile.covidinfo.model.covid.retrofit.global.objects

import com.google.gson.annotations.SerializedName

data class GlobalSummary (

		@SerializedName("NewConfirmed") val newConfirmed : Int,
		@SerializedName("TotalConfirmed") val totalConfirmed : Int,
		@SerializedName("NewDeaths") val newDeaths : Int,
		@SerializedName("TotalDeaths") val totalDeaths : Int,
		@SerializedName("NewRecovered") val newRecovered : Int,
		@SerializedName("TotalRecovered") val totalRecovered : Int
)