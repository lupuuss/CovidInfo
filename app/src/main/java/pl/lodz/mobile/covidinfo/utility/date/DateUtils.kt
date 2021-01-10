package pl.lodz.mobile.covidinfo.utility.date

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun String.parseAsIsoDate(locale: Locale): Date? {
        val formatter = SimpleDateFormat("", locale)
        return formatter.parse(this)
    }
}