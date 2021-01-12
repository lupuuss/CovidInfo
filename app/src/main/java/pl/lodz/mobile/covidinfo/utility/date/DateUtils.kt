package pl.lodz.mobile.covidinfo.utility.date

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun String.parseAsIsoDate(locale: Locale): Date? {
        val formatter = SimpleDateFormat("yyyy-MM-DD'T'hh:mm:ss'Z'", locale)
        return formatter.parse(this)
    }
}