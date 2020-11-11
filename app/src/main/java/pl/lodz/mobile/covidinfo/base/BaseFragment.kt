package pl.lodz.mobile.covidinfo.base

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment(), BaseView {
    override fun showQuickDialog(message: String, actionName: String?, action: () -> Unit) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).apply {
            if (actionName != null) {
                setAction(actionName) { action() }
            }

            show()
        }
    }

    protected val baseActivity: BaseActivity
        get() = activity as BaseActivity

}