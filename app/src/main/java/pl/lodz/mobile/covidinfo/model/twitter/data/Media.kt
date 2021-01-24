package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class Media(
        @SerializedName("media_key") val mediaKey : String,
        @SerializedName("type") val type : String,
        @SerializedName("url") val url : String
)
