package pl.lodz.mobile.covidinfo.location.retrofit

import com.google.gson.annotations.SerializedName

data class Address (
        @SerializedName("house_number") val houseNumber: String?,
        @SerializedName("road") val road: String?,
        @SerializedName("suburb") val suburb: String?,
        @SerializedName("city_district") val cityDistrict: String?,
        @SerializedName("state") val state: String?,
        @SerializedName("postcode") val postcode: String,
        @SerializedName("country") val country: String,
        @SerializedName("country_code") val countryCode: String
)