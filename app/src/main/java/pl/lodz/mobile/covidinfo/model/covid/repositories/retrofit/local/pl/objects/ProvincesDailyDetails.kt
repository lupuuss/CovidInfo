package pl.lodz.mobile.covidinfo.model.covid.repositories.retrofit.local.pl.objects

import java.lang.IllegalArgumentException

@Suppress("SpellCheckingInspection")
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
) {

	fun getByName(name: String): Int {
		return when (name) {
			"Dolnośląskie" -> dolnoslaskie
			"Mazowieckie" -> mazowieckie
			"Lubelskie" -> lubelskie
			"Wielkopolskie" -> wielkopolskie
			"Podkarpackie" -> podkarpackie
			"Śląskie" -> slaskie
			"Lubuskie" -> lubuskie
			"Podlaskie" -> podlaskie
			"Zachodniopomorskie" -> zachodniopomorskie
			"Kujawsko-Pomorskie" -> kujawsko_pomorskie
      		"Świętokrzyskie" -> swietokrzyskie
			"Łódzkie" -> lodzkie
			"Małopolskie" -> malopolskie
			"Warmińsko-Mazurskie" -> warminsko_mazurskie
			"Opolskie" -> opolskie
			"Pomorskie" -> pomorskie
			else -> throw IllegalArgumentException("Not supported province name!")
		}
	}
}