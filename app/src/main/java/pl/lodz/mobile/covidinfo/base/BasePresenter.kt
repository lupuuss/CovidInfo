package pl.lodz.mobile.covidinfo.base

abstract class BasePresenter<T : BaseView> : BasePresenterActions<T> {

    protected var view: T? = null

    override fun init(view: T) {
        this.view = view
    }

    override fun close() {}
}