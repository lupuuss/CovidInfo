package pl.lodz.mobile.covidinfo.modules.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.koin.android.scope.currentScope
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseActivity

class MainActivity : BaseActivity(), MainContract.View {

    private val presenter: MainContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.init(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings -> presenter.goToSettings()
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            presenter.close()
        }
    }

    override fun navigateToSettings() {
        TODO("Not yet implemented")
    }
}