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
    fun setTravelId(id: String) {
        editor = preferences.edit()
        editor.putString("TRAVEL_ID", id)
        editor.apply()
    }

    fun getTravelId() = preferences.getString("TRAVEL_ID", "")

    //////////////////////////////////////////////////////////
    fun clearAllData() {
        preferences.edit { clear() }
    }

}
