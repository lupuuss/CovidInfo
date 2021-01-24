package pl.lodz.mobile.covidinfo

import androidx.appcompat.app.AppCompatActivity

object AppTheme {

    enum class Name(val id: Int) {
        Dark(R.style.Theme_CovidInfo_Dark), Light(R.style.Theme_CovidInfo)
    }

    private val sharedName = "AppThemeShared"
    private val themeKey = "Theme"

    fun get(activity: AppCompatActivity): Name {

        val pref = activity.getSharedPreferences(sharedName, AppCompatActivity.MODE_PRIVATE)
        val value = pref.getString(themeKey, Name.Light.name)!!

        return Name.valueOf(value)
    }

    fun set(activity: AppCompatActivity, name: Name) {
        val pref = activity.getSharedPreferences(sharedName, AppCompatActivity.MODE_PRIVATE)
        pref.edit()
            .putString(themeKey, name.name)
            .apply()

        activity.setTheme(name.id)
    }

    fun restore(activity: AppCompatActivity) {
        val name = get(activity)

        activity.setTheme(name.id)
    }

}