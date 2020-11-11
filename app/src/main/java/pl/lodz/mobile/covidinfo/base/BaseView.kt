package pl.lodz.mobile.covidinfo.base

interface BaseView {

    fun showQuickDialog(message: String, actionName: String? = null, action: () -> Unit = {})
}