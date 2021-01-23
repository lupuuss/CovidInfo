package pl.lodz.mobile.covidinfo.location.retrofit

import com.google.gson.annotations.SerializedName

data class GeoCoding(
        @SerializedName("lat") val lat : Double,
        @SerializedName("lng") val lng : Double,
        @SerializedName("display_name") val displayName : String,
        @SerializedName("address") val address : Address
)
