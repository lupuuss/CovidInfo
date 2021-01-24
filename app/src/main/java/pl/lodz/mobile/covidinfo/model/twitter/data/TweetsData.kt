package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class TweetsData (

		@SerializedName("data") val data : List<Tweet>,
		@SerializedName("meta") val meta : MetaData,
		@SerializedName("includes") val includes : Includes,
)