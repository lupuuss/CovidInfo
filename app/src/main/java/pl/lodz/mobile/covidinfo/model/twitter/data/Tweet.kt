package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Tweet (

		@SerializedName("id") val id : Int,
		@SerializedName("text") val text : String,
		@SerializedName("created_at") val createAt : LocalDateTime
)