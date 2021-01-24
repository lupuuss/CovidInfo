package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class Attachments (
        @SerializedName("media_keys") val mediaKeys : List<String>
)