package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class Includes (
        @SerializedName("media") val media : List<Media>
)