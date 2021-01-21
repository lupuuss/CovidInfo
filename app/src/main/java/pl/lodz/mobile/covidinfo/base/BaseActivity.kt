package pl.lodz.mobile.covidinfo.base

import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


abstract class BaseActivity : AppCompatActivity(), BaseView {

    override fun showQuickDialog(message: String, actionName: String?, action: () -> Unit) {
        Snackbar.make(
            window.decorView.rootView,
            message,
            Snackbar.LENGTH_LONG
        ).apply {

            if (actionName != null) {
                setAction(actionName) { action() }
            }

            show()
        }
    }
}