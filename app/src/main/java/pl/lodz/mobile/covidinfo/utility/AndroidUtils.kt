package pl.lodz.mobile.covidinfo.utility

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.SpinnerAdapter
import kotlin.math.roundToInt


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