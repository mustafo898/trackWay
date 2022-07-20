package dark.composer.trackway.data.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPref(context: Context) {

    private var preferences: SharedPreferences =
        context.getSharedPreferences("MAP", MODE_PRIVATE)

    private lateinit var editor: SharedPreferences.Editor

    ////////////////////////////////////////////////////////
    fun setUsername(username: String) {
        editor = preferences.edit()
        editor.putString("USER_NAME", username)
        editor.apply()
    }

    fun getUsername() = preferences.getString("USER_NAME", "")

    //////////////////////////////////////////////////////////
    fun setTravelName(name: String) {
        editor = preferences.edit()
        editor.putString("TRAVEL", name)
        editor.apply()
    }

    fun getTravelName() = preferences.getString("TRAVEL", "")

    //////////////////////////////////////////////////////////

    fun setTheme(num:Int){
        editor = preferences.edit()
        editor.putInt("THEME",num)
        editor.apply()
    }

    fun getTheme() = preferences.getInt("THEME",0)

    //////////////////////////////////////////////////////////

    fun setMode(check:Boolean){
        editor = preferences.edit()
        editor.putBoolean("MODE",check)
        editor.apply()
    }

    fun getMode() = preferences.getBoolean("MODE", false)

    //////////////////////////////////////////////////////////
    fun clearAllData() {
        preferences.edit { clear() }
    }

}
