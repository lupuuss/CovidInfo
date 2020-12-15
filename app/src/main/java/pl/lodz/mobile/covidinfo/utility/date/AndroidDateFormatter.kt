package pl.lodz.mobile.covidinfo.utility.date

import android.content.Context
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class AndroidDateFormatter(
    private val timeProvider: () -> Long,
    locale: Locale
) : DateFormatter {

    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", locale)

    override fun getRelativeDateStringFromIsoDate(isoDate: String): String {
        val date = isoFormatter.parse(isoDate) ?: return isoDate

        return DateUtils
            .getRelativeTimeSpanString(date.time, timeProvider(), DateUtils.SECOND_IN_MILLIS)
            .toString()
    }
}