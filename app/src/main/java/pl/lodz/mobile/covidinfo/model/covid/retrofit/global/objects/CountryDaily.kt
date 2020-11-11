package pl.lodz.mobile.covidinfo.model.covid.retrofit.global.objects

import com.google.gson.annotations.SerializedName

data class CountryDaily (

		@SerializedName("Country") val country : String,
		@SerializedName("CountryCode") val countryCode : String,
		@SerializedName("Province") val province : String,
		@SerializedName("City") val city : String,
		@SerializedName("CityCode") val cityCode : String,
		@SerializedName("Lat") val lat : Double,
		@SerializedName("Lon") val lon : Double,
		@SerializedName("Confirmed") val confirmed : Int,
		@SerializedName("Deaths") val deaths : Int,
		@SerializedName("Recovered") val recovered : Int,
		@SerializedName("Active") val active : Int,
		@SerializedName("Date") val date : String
)