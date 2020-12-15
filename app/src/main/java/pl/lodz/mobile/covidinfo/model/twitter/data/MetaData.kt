package pl.lodz.mobile.covidinfo.model.twitter.data

import com.google.gson.annotations.SerializedName

data class MetaData (

		@SerializedName("newest_id") val newestId : Int,
		@SerializedName("oldest_id") val oldestId : Int,
		@SerializedName("result_count") val resultCount : Int,
		@SerializedName("next_token") val nextToken : String
)