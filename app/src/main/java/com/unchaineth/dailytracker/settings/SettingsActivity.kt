package com.unchaineth.dailytracker.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.unchaineth.dailytracker.R
import com.unchaineth.dailytracker.R.id.button
import com.unchaineth.dailytracker.main.MainActivity


class SettingsActivity : AppCompatActivity() {

    private lateinit var themePreferences: ThemePreferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesNotifications: SharedPreferences
    //private lateinit var switchDarkMode: SwitchCompat
    private lateinit var switchNotifications: SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)

        setTheme(themePreferences.getSavedTheme())

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //sharedPreferences = getSharedPreferences("darkMode", MODE_PRIVATE)
        //switchDarkMode = findViewById(R.id.switchDarkMode)
        switchNotifications = findViewById(R.id.switchNotifications)

/*        val darkModeOn = sharedPreferences.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = darkModeOn

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            recreate()

        }*/





        val themeRadioGroup: RadioGroup = findViewById(R.id.themeRadioGroup)
        themeRadioGroup.setOnCheckedChangeListener(themeChangeListener)

        val tvSelectTopics: TextView = findViewById(R.id.tvSelectTopics)

        tvSelectTopics.setOnClickListener {
            val intent = Intent(this, SelectTopicsActivity::class.java)
            startActivity(intent)
        }



        sharedPreferencesNotifications = getSharedPreferences("notifications", MODE_PRIVATE)
        switchNotifications.isChecked =
            sharedPreferencesNotifications.getBoolean("daily_notifications", true)

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesNotifications.edit().putBoolean("daily_notifications", isChecked)
                .apply()

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("daily")
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("daily")
            }

            FirebaseMessaging.getInstance().subscribeToTopic("daily")

        }
    }

    private val themeChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        when (checkedId) {
            R.id.themeRedRadioButton -> setThemeAndSaveSelection(MainActivity.AppTheme.RED)
            R.id.themeGreenRadioButton -> setThemeAndSaveSelection(MainActivity.AppTheme.GREEN)
            R.id.themeBlueRadioButton -> setThemeAndSaveSelection(MainActivity.AppTheme.BLUE)
            R.id.themeOrangeRadioButton -> setThemeAndSaveSelection(MainActivity.AppTheme.ORANGE)
            R.id.themeBlackRadioButton -> setThemeAndSaveSelection(MainActivity.AppTheme.BLACK)
            R.id.themeDefaultRadioButton -> setThemeAndSaveSelection(MainActivity.AppTheme.DEFAULT)
        }

    }

    private fun setThemeAndSaveSelection(theme: Int) {
        themePreferences.saveTheme(theme)
        setTheme(theme)
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainActivityIntent)
        finish()
    }


}