package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class LocalStorage {
    enum class PreferenceKeys(val value: String) {
        TOKEN("TOKEN")
    }

    companion object {
        private const val PREFERENCE_ID = "SIMPLECAM_PREF"

        private fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                PREFERENCE_ID,
                Context.MODE_PRIVATE
            )
        }

        @SuppressLint("ApplySharedPref")
        fun set(context: Context, key: String, value: String) {
            val sharedPreferences = getPreferences(context)
            val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
            sharedPreferencesEditor.putString(key, value)
            sharedPreferencesEditor.commit()
        }

        fun get(context: Context, key: String): String? {
            val sharedPreferences = getPreferences(context)
            return sharedPreferences.getString(key, null)
        }
    }
}