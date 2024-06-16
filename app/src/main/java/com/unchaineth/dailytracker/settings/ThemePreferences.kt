package com.unchaineth.dailytracker.settings

import android.content.Context
import android.content.SharedPreferences
import com.unchaineth.dailytracker.main.MainActivity

class ThemePreferences(context: Context) {

    companion object {
        private const val PREF_NAME = "theme_pref"
        private const val KEY_THEME = "selected_theme"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveTheme(theme: Int) {
        sharedPreferences.edit().putInt(KEY_THEME, theme).apply()
    }

    fun getSavedTheme(): Int {
        return sharedPreferences.getInt(KEY_THEME, MainActivity.AppTheme.RED)
    }
}