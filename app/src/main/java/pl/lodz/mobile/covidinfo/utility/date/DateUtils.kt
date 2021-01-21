package pl.lodz.mobile.covidinfo.utility.date

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object DateUtils {
    fun String.parseAsIsoDate(locale: Locale): Date? = this.parseDate(locale, "yyyy-MM-DD'T'hh:mm:ss'Z'")

    fun String.parseDate(locale: Locale, pattern: String): Date? {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.parse(this)
    }
}