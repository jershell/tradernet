package ru.tradernet.data.repository


import android.content.Context
import android.preference.PreferenceManager

class PersistentRepository(val appCtx: Context) {
    inline fun <reified T> save(key: String, value: T) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(appCtx)!!

        when (T::class) {
            String::class -> preferences.edit().putString(key, value as String).apply()
            Int::class -> preferences.edit().putInt(key, value as Int).apply()
            Boolean::class -> preferences.edit().putBoolean(key, value as Boolean).apply()
            Float::class -> preferences.edit().putFloat(key, value as Float).apply()
            Long::class -> preferences.edit().putLong(key, value as Long).apply()
            else -> throw IllegalArgumentException("PreferenceManager hasn't getter with type")
        }
    }

    fun remove(key: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(appCtx)!!
        preferences
            .edit()
            .remove(key)
            .apply()
    }

    inline fun <reified T> read(key: String, defaultValue: T): T {
        val pm = PreferenceManager
            .getDefaultSharedPreferences(appCtx)!!

        return when (T::class) {
            String::class -> pm.getString(key, defaultValue as String) as T
            Int::class -> pm.getInt(key, defaultValue as Int) as T
            Boolean::class -> pm.getBoolean(key, defaultValue as Boolean) as T
            Float::class -> pm.getFloat(key, defaultValue as Float) as T
            Long::class -> pm.getLong(key, defaultValue as Long) as T
            else -> throw IllegalArgumentException("PreferenceManager hasn't getter with type")
        }
    }
}