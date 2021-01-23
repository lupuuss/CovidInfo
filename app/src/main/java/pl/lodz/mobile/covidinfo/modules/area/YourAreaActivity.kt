package pl.lodz.mobile.covidinfo.modules.area

import android.os.Bundle
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity

class YourAreaActivity : BaseActivity(), YourAreaContract.View {

    override var isLoading: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isContentVisible: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isContentLoadingError: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_area)
    }
}