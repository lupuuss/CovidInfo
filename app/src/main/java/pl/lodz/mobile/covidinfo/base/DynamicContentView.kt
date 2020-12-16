package pl.lodz.mobile.covidinfo.base

interface DynamicContentView : BaseView {
    var isLoading: Boolean
    var isContentVisible: Boolean
    var isContentLoadingError: Boolean
}