package pl.lodz.mobile.covidinfo.base

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.utility.getColorForAttr


abstract class BaseActivity : AppCompatActivity(), BaseView {

    override fun showQuickDialog(message: String, positive: Boolean, actionName: String?, action: () -> Unit) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).apply {

            setBackgroundTint(getColorForAttr(if (positive) R.attr.colorPositive else R.attr.colorNegative))


            if (actionName != null) {
                setAction(actionName) { action() }
            }

            show()
        }
    }
}