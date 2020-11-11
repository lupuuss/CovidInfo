package pl.lodz.mobile.covidinfo.base

interface BasePresenterActions<T : BaseView> {
    fun init(view: T)
    fun close()
}