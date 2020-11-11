package pl.lodz.mobile.covidinfo.model.covid.retrofit.local.pl.objects

import com.google.gson.annotations.SerializedName

data class ProvincesDailyDetails (
	// skipped _id
	val date : String,
	val mazowieckie : Int,
	val slaskie : Int,
	val malopolskie : Int,
	val wielkopolskie : Int,
	val lodzkie : Int,
	val kujawsko_pomorskie : Int,
	val podkarpackie : Int,
	val pomorskie : Int,
	val dolnoslaskie : Int,
	val lubelskie : Int,
	val swietokrzyskie : Int,
	val zachodniopomorskie : Int,
	val opolskie : Int,
	val podlaskie : Int,
	val warminsko_mazurskie : Int,
	val lubuskie : Int
)