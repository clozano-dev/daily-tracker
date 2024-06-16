package com.unchaineth.dailytracker.settings

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unchaineth.dailytracker.R
import com.unchaineth.dailytracker.main.MainActivity
import java.util.Calendar

class AccountActivity : AppCompatActivity() {

    private lateinit var etBirthdayDate: EditText
    private lateinit var selectedDate: Calendar
    private lateinit var sharedPreferencesBirthday: SharedPreferences
    private lateinit var sharedPreferencesHoroscope: SharedPreferences
    private lateinit var spinnerHoroscope: Spinner

    private lateinit var themePreferences: ThemePreferences

    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)

        setTheme(themePreferences.getSavedTheme())


        enableEdgeToEdge()
        setContentView(R.layout.activity_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.accountMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etBirthdayDate = findViewById(R.id.etBirthdayDate)
        spinnerHoroscope = findViewById(R.id.spinnerHoroscope)

        selectedDate = Calendar.getInstance()
        sharedPreferencesBirthday = getSharedPreferences("Birthday", Context.MODE_PRIVATE)
        sharedPreferencesHoroscope = getSharedPreferences("Horoscope", Context.MODE_PRIVATE)

        val savedDateString = sharedPreferencesBirthday.getString("selectedDate", null)

        val zodiacSigns = resources.getStringArray(R.array.zodiac_signs)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, zodiacSigns)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHoroscope.adapter = adapter

        val savedHoroscopeIndex = sharedPreferencesHoroscope.getInt("selectedZodiacIndex", 0)
        spinnerHoroscope.setSelection(savedHoroscopeIndex)

        spinnerHoroscope.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferencesHoroscope.edit().putInt("selectedZodiacIndex", position).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })

        if (savedDateString != null){
            val savedDateArray = savedDateString.split("/")
            val savedDay = savedDateArray[0].toInt()
            val savedMonth = savedDateArray[1].toInt() - 1
            val savedYear = savedDateArray[2].toInt()

            selectedDate = Calendar.getInstance()
            selectedDate.set(savedYear, savedMonth, savedDay)

            selectedDate.set(savedYear, savedMonth, savedDay)
        } else {
            selectedDate = Calendar.getInstance()
        }

        etBirthdayDate.setOnClickListener{
            showDatePickerDialog()
        }

        button = findViewById(R.id.button)
        button.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainActivityIntent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        val savedDateString = sharedPreferencesBirthday.getString("selectedDate", null)
        if (savedDateString != null) {
            etBirthdayDate.setText(savedDateString)
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)

                val formattedDate = "%02d/%02d/%d".format(dayOfMonth, month + 1, year)
                sharedPreferencesBirthday.edit().putString("selectedDate", formattedDate).apply()
                etBirthdayDate.setText(formattedDate)

                val zodiacSign = getZodiacSign(dayOfMonth, month)
                sharedPreferencesBirthday.edit().putString("zodiacSign", zodiacSign).apply()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH),
        )
        datePickerDialog.show()
    }

    private fun getZodiacSign(day: Int, month: Int): String {
        return when (month + 1) {
            1 -> if (day < 20) "Capricorn" else "Aquarius"
            2 -> if (day < 19) "Aquarius" else "Pisces"
            3 -> if (day < 21) "Pisces" else "Aries"
            4 -> if (day < 20) "Aries" else "Taurus"
            5 -> if (day < 21) "Taurus" else "Gemini"
            6 -> if (day < 22) "Gemini" else "Cancer"
            7 -> if (day < 23) "Cancer" else "Leo"
            8 -> if (day < 23) "Leo" else "Virgo"
            9 -> if (day < 23) "Virgo" else "Libra"
            10 -> if (day < 23) "Libra" else "Scorpio"
            11 -> if (day < 22) "Scorpio" else "Sagittarius"
            12 -> if (day < 22) "Sagittarius" else "Capricorn"
            else -> ""
        }
    }

}