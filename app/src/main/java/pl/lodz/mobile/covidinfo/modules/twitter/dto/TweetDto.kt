package pl.lodz.mobile.covidinfo.modules.twitter.dto

data class TweetDto(
        val userName: String,
        val userImageLink: String,
        val text: String,
        val createdAt: String
)