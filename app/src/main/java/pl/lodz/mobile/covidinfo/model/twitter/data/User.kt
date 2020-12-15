package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("id") val id: Int,
        @SerializedName("username") val userName: String,
        @SerializedName("name") val name: String,
        @SerializedName("profile_image_url") val profileImageUrl: String
)
