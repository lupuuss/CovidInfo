package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data") val data: User?
)