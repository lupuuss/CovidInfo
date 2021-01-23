package pl.lodz.mobile.covidinfo.base

interface BaseView {

    fun showQuickDialog(
        message: String,
        positive: Boolean = false,
        actionName: String? = null,
        action: () -> Unit = {}
    )
}