package pl.lodz.mobile.covidinfo.base

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.utility.getColorForAttr

abstract class BaseFragment : Fragment(), BaseView {
    override fun showQuickDialog(
        message: String,
        positive: Boolean,
        actionName: String?,
        action: () -> Unit
    ) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).apply {

            setBackgroundTint(
                requireContext().getColorForAttr(if (positive) R.attr.colorPositive else R.attr.colorNegative))

            if (actionName != null) {
                setAction(actionName) { action() }
            }

            show()
        }
    }

    protected val baseActivity: BaseActivity
        get() = activity as BaseActivity

}