package pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.global.objects

import com.google.gson.annotations.SerializedName

data class Country (

		@SerializedName("Country") val name : String,
		@SerializedName("Slug") val slug : String,
		@SerializedName("ISO2") val iSO2 : String
)