package pl.lodz.mobile.covidinfo.utility

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt


fun dpToPixels(context: Context, dp: Int): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}