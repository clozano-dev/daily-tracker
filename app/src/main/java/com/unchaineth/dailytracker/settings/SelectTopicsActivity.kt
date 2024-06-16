package com.unchaineth.dailytracker.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unchaineth.dailytracker.R
import com.unchaineth.dailytracker.main.MainActivity

class SelectTopicsActivity : AppCompatActivity() {

    private lateinit var themePreferences: ThemePreferences

    private lateinit var switchYearProgress: SwitchCompat
    private lateinit var switchBirthday: SwitchCompat
    private lateinit var switchAnniversaries: SwitchCompat
    private lateinit var switchMainHoroscope: SwitchCompat
    private lateinit var switchSecondaryHoroscope: SwitchCompat
    private lateinit var switchFestivals: SwitchCompat
    private lateinit var switchMoonPhases: SwitchCompat
    private lateinit var switchQuotes: SwitchCompat

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)

        setTheme(themePreferences.getSavedTheme())

        enableEdgeToEdge()
        setContentView(R.layout.activity_select_topics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        switchYearProgress = findViewById(R.id.switchYearProgress)
        switchBirthday = findViewById(R.id.switchBirthday)
        switchAnniversaries = findViewById(R.id.switchAnniversaries)
        switchMainHoroscope = findViewById(R.id.switchMainHoroscope)
        switchSecondaryHoroscope = findViewById(R.id.switchSecondaryHoroscope)
        switchFestivals = findViewById(R.id.switchFestivals)
        switchMoonPhases = findViewById(R.id.switchMoonPhases)
        switchQuotes = findViewById(R.id.switchQuotes)

        button = findViewById(R.id.button)
        button.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainActivityIntent)
            finish()
        }

        sharedPreferences = getSharedPreferences("Topics", Context.MODE_PRIVATE)

        val switchYearProgressState = sharedPreferences.getBoolean("switchYearProgress", true)
        switchYearProgress.isChecked = switchYearProgressState

        switchYearProgress.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchYearProgress", isChecked)
            editor.apply()
        }

        val switchBirthdayState = sharedPreferences.getBoolean("switchBirthday", true)
        switchBirthday.isChecked = switchBirthdayState

        switchBirthday.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchBirthday", isChecked)
            editor.apply()
        }

        val switchAnniversariesState = sharedPreferences.getBoolean("switchAnniversaries", true)
        switchAnniversaries.isChecked = switchAnniversariesState

        switchAnniversaries.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchAnniversaries", isChecked)
            editor.apply()
        }

        val switchMainHoroscopeState = sharedPreferences.getBoolean("switchMainHoroscope", true)
        switchMainHoroscope.isChecked = switchMainHoroscopeState

        switchMainHoroscope.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchMainHoroscope", isChecked)
            editor.apply()
        }

        val switchSecondaryHoroscopeState = sharedPreferences.getBoolean("switchSecondaryHoroscope", true)
        switchSecondaryHoroscope.isChecked = switchSecondaryHoroscopeState

        switchSecondaryHoroscope.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchSecondaryHoroscope", isChecked)
            editor.apply()
        }

        val switchFestivalsState = sharedPreferences.getBoolean("switchFestivals", true)
        switchFestivals.isChecked = switchFestivalsState

        switchFestivals.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchFestivals", isChecked)
            editor.apply()
        }

        val switchMoonPhasesState = sharedPreferences.getBoolean("switchMoonPhases", true)
        switchMoonPhases.isChecked = switchMoonPhasesState

        switchMoonPhases.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchMoonPhases", isChecked)
            editor.apply()
        }

        val switchQuotesState = sharedPreferences.getBoolean("switchQuotes", true)
        switchQuotes.isChecked = switchQuotesState

        switchQuotes.setOnCheckedChangeListener { _, isChecked ->
            editor = sharedPreferences.edit()
            editor.putBoolean("switchQuotes", isChecked)
            editor.apply()
        }


    }
}