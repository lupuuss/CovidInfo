package pl.lodz.mobile.covidinfo.utility

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.text.HtmlCompat
import java.util.regex.Pattern
import kotlin.math.roundToInt

@ColorInt
fun Context.getColorForAttr(@AttrRes attr: Int): Int {

    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

@Suppress("DEPRECATION")
fun String.htmlToSpanned(): Spanned {

    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        Html.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this)
    }
}

fun String.boldQuery(query: String): String {

    if (query == "") return this

    val strB = StringBuilder()
    this.split(Pattern.compile("((?<=$query)|(?=$query))", Pattern.CASE_INSENSITIVE)).forEach {

        if (it.contains(query, true)) {

            strB.append("<b>")
            strB.append(it)
            strB.append("</b>")

        } else {

            strB.append(it)
        }

    }
    return strB.toString()
}

fun dpToPixels(context: Context, dp: Float): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

class OnlyUserSelectionListener(
    private val onUserSelection: (Int) -> Unit
) : AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private var touch = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        touch = true
        return false
    }

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {

        if (touch) {
            onUserSelection(position)
            touch = false
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

}