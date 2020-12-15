package pl.lodz.mobile.covidinfo.utility.date

interface DateFormatter {
    fun getRelativeDateStringFromIsoDate(isoDate: String): String
}