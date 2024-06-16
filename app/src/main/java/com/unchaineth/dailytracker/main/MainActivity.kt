package com.unchaineth.dailytracker.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unchaineth.dailytracker.R
import com.unchaineth.dailytracker.R.style.AppThemeBlack
import com.unchaineth.dailytracker.R.style.AppThemeBlue
import com.unchaineth.dailytracker.R.style.AppThemeGreen
import com.unchaineth.dailytracker.R.style.AppThemeOrange
import com.unchaineth.dailytracker.R.style.AppThemeRed
import com.unchaineth.dailytracker.R.style.Base_Theme_DailyTracker
import com.unchaineth.dailytracker.login.LoginActivity
import com.unchaineth.dailytracker.login.LoginActivity.Companion.userEmail
import com.unchaineth.dailytracker.settings.AccountActivity
import com.unchaineth.dailytracker.settings.SettingsActivity
import com.unchaineth.dailytracker.settings.ThemePreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var themePreferences: ThemePreferences

    private lateinit var tvAnniversariesDescription: TextView
    private lateinit var tvBirthdayDescription: TextView
    private lateinit var tvHoroscopeDescription: TextView
    private lateinit var tvHoroscopeBirthdayDescription: TextView
    private lateinit var tvYearProgressDescription: TextView
    private lateinit var tvFestivalsDescription: TextView
    private lateinit var tvMoonPhasesDescription: TextView
    private lateinit var tvQuoteDescription: TextView

    private lateinit var tvHoroscopeTitle: TextView
    private lateinit var tvHoroscopeBirthdayTitle: TextView

    private lateinit var firestoreDB: FirebaseFirestore

    private lateinit var cvYearProgress: CardView
    private lateinit var cvBirthday: CardView
    private lateinit var cvAnniversaries: CardView
    private lateinit var cvHoroscope: CardView
    private lateinit var cvHoroscopeBirthday: CardView
    private lateinit var cvFestivals: CardView
    private lateinit var cvMoonPhases: CardView
    private lateinit var cvQuotes: CardView

    private var yearProgressStatus: Boolean = false
    private var birthdayStatus: Boolean = false
    private var anniversariesStatus: Boolean = false
    private var horoscopeStatus: Boolean = false
    private var horoscopeBirthdayStatus: Boolean = false
    private var festivalsStatus: Boolean = false
    private var moonPhasesStatus: Boolean = false
    private var quotesStatus: Boolean = false

    private lateinit var drawer: DrawerLayout
    private lateinit var lyAdsBanner: LinearLayout

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesBirthday: SharedPreferences
    private lateinit var sharedPreferencesHoroscope: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)


        setTheme(themePreferences.getSavedTheme())

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initToolBar()
        initNavigationView()

        initAds()

        tvAnniversariesDescription = findViewById(R.id.tvAnniversariesDescription)
        tvBirthdayDescription = findViewById(R.id.tvBirthdayDescription)
        tvHoroscopeDescription = findViewById(R.id.tvHoroscopeDescription)
        tvHoroscopeBirthdayDescription = findViewById(R.id.tvHoroscopeBirthdayDescription)
        tvYearProgressDescription = findViewById(R.id.tvYearProgressDescription)
        tvFestivalsDescription = findViewById(R.id.tvFestivalsDescription)
        tvMoonPhasesDescription = findViewById(R.id.tvMoonPhasesDescription)
        tvQuoteDescription = findViewById(R.id.tvQuoteDescription)

        tvHoroscopeTitle = findViewById(R.id.tvHoroscopeTitle)
        tvHoroscopeBirthdayTitle = findViewById(R.id.tvHoroscopeBirthdayTitle)

        cvYearProgress = findViewById(R.id.cvYearProgress)
        cvBirthday = findViewById(R.id.cvBirthday)
        cvAnniversaries = findViewById(R.id.cvAnniversaries)
        cvHoroscope = findViewById(R.id.cvHoroscope)
        cvHoroscopeBirthday = findViewById(R.id.cvHoroscopeBirthday)
        cvFestivals = findViewById(R.id.cvFestivals)
        cvMoonPhases = findViewById(R.id.cvMoonPhases)
        cvQuotes = findViewById(R.id.cvQuotes)

        sharedPreferences = getSharedPreferences("Topics", Context.MODE_PRIVATE)
        sharedPreferencesBirthday = getSharedPreferences("Birthday", Context.MODE_PRIVATE)
        sharedPreferencesHoroscope = getSharedPreferences("Horoscope", Context.MODE_PRIVATE)

        yearProgressStatus = sharedPreferences.getBoolean("switchYearProgress", true)
        if (!yearProgressStatus) {
            cvYearProgress.visibility = View.GONE
        }

        birthdayStatus = sharedPreferences.getBoolean("switchBirthday", true)
        if (!birthdayStatus) {
            cvBirthday.visibility = View.GONE
        }

        anniversariesStatus = sharedPreferences.getBoolean("switchAnniversaries", true)
        if (!anniversariesStatus) {
            cvAnniversaries.visibility = View.GONE
        }

        horoscopeStatus = sharedPreferences.getBoolean("switchMainHoroscope", true)
        if (!horoscopeStatus) {
            cvHoroscope.visibility = View.GONE
        }

        horoscopeBirthdayStatus = sharedPreferences.getBoolean("switchSecondaryHoroscope", true)
        if (!horoscopeBirthdayStatus) {
            cvHoroscopeBirthday.visibility = View.GONE
        }

        festivalsStatus = sharedPreferences.getBoolean("switchFestivals", true)
        if (!festivalsStatus) {
            cvFestivals.visibility = View.GONE
        }

        moonPhasesStatus = sharedPreferences.getBoolean("switchMoonPhases", true)
        if (!moonPhasesStatus) {
            cvMoonPhases.visibility = View.GONE
        }

        quotesStatus = sharedPreferences.getBoolean("switchQuotes", true)
        if (!quotesStatus) {
            cvQuotes.visibility = View.GONE
        }

        sharedPreferences = getSharedPreferences("Topics", Context.MODE_PRIVATE)
        val yearProgressStatus = sharedPreferences.getBoolean("switchYearProgress", true)

        if (!yearProgressStatus) {
            cvYearProgress.visibility = View.GONE
        } else {
            cvYearProgress.visibility = View.VISIBLE
        }

        firestoreDB = FirebaseFirestore.getInstance()

        val formatoFecha = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val fechaActual = formatoFecha.format(Calendar.getInstance().time)

        firestoreDB.collection("Day").document(fechaActual).get().addOnSuccessListener { document ->
                if (document != null) {

                    val anniversary = document.getString("Anniversary")
                    val birthday = document.getString("Birthday")
                    val festivals = document.getString("Festival")
                    val moonPhase = document.getString("MoonPhase")
                    val quote = document.getString("Quote")

                    val horoscopeBirthdayStored = getZodiacSignFromBirthday()

                    when (horoscopeBirthdayStored) {
                        "Capricorn" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Capricorn")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.capricornio)
                        }

                        "Aquarius" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Aquarius")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.aquarius)
                        }

                        "Pisces" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Pisces")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.piscis)
                        }

                        "Aries" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Aries")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.aries)
                        }

                        "Taurus" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Taurus")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.taurus)
                        }

                        "Gemini" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Gemini")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.gemini)
                        }

                        "Cancer" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Cancer")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.cancer)
                        }

                        "Leo" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Leo")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.leo)
                        }

                        "Virgo" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Virgo")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.virgo)
                        }

                        "Libra" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Libra")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.libra)
                        }

                        "Scorpio" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Scorpio")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.scorpio)
                        }

                        "Sagittarius" -> {
                            tvHoroscopeBirthdayDescription.text = document.getString("Sagittarius")
                            tvHoroscopeBirthdayTitle.text = getString(R.string.sagittarius)
                        }

                        else -> {
                            tvHoroscopeBirthdayDescription.text =
                                getString(R.string.select_birthday)
                        }
                    }

                    val horoscopeStored = getZodiacSignFromPreferences()

                    when (horoscopeStored) {
                        0 -> {
                            tvHoroscopeDescription.text =
                                getString(R.string.select_horoscope_account)
                        }

                        1 -> {
                            tvHoroscopeDescription.text = document.getString("Capricorn")
                            tvHoroscopeTitle.text = getString(R.string.capricornio)
                        }

                        2 -> {
                            tvHoroscopeDescription.text = document.getString("Aquarius")
                            tvHoroscopeTitle.text = getString(R.string.aquarius)
                        }

                        3 -> {
                            tvHoroscopeDescription.text = document.getString("Pisces")
                            tvHoroscopeTitle.text = getString(R.string.piscis)
                        }

                        4 -> {
                            tvHoroscopeDescription.text = document.getString("Aries")
                            tvHoroscopeTitle.text = getString(R.string.aries)
                        }

                        5 -> {
                            tvHoroscopeDescription.text = document.getString("Taurus")
                            tvHoroscopeTitle.text = getString(R.string.taurus)
                        }

                        6 -> {
                            tvHoroscopeDescription.text = document.getString("Gemini")
                            tvHoroscopeTitle.text = getString(R.string.gemini)
                        }

                        7 -> {
                            tvHoroscopeDescription.text = document.getString("Cancer")
                            tvHoroscopeTitle.text = getString(R.string.cancer)
                        }

                        8 -> {
                            tvHoroscopeDescription.text = document.getString("Leo")
                            tvHoroscopeTitle.text = getString(R.string.leo)
                        }

                        9 -> {
                            tvHoroscopeDescription.text = document.getString("Virgo")
                            tvHoroscopeTitle.text = getString(R.string.virgo)
                        }

                        10 -> {
                            tvHoroscopeDescription.text = document.getString("Libra")
                            tvHoroscopeTitle.text = getString(R.string.libra)
                        }

                        11 -> {
                            tvHoroscopeDescription.text = document.getString("Scorpio")
                            tvHoroscopeTitle.text = getString(R.string.scorpio)
                        }

                        12 -> {
                            tvHoroscopeDescription.text = document.getString("Sagittarius")
                            tvHoroscopeTitle.text = getString(R.string.sagittarius)
                        }

                        else -> {
                            tvHoroscopeDescription.text =
                                getString(R.string.select_horoscope_account)

                        }
                    }

                    val yearPercentageDouble = yearProgressCalculation()


                    val yearPercentageString = String.format("%.2f", yearPercentageDouble)
                    tvYearProgressDescription.text = "Llevamos un $yearPercentageString% del año"

                    tvAnniversariesDescription.text = anniversary
                    tvBirthdayDescription.text = birthday
                    tvFestivalsDescription.text = festivals
                    tvMoonPhasesDescription.text = moonPhase
                    tvQuoteDescription.text = quote

                } else {
                    tvAnniversariesDescription.text = "No hay ninguna efeméride interesante hoy"
                }
            }.addOnFailureListener { exception ->
                tvAnniversariesDescription.text = "Error al obtener los datos: $exception"
            }


    }


    private fun getZodiacSignFromBirthday(): String? {
        return sharedPreferencesBirthday.getString("zodiacSign", null)
    }

    private fun getZodiacSignFromPreferences(): Int {
        return sharedPreferencesHoroscope.getInt("selectedZodiacIndex", 0)

    }



    private fun initAds() {

        MobileAds.initialize(this) {}

        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"

        lyAdsBanner = findViewById(R.id.lyAdsBanner)
        lyAdsBanner.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun initToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.main)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.DailyTracker, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun initNavigationView() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val headerView: View =
            LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)
    }


    private fun signOut() {
        userEmail = ""
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_account -> {
                callAccountActivity()
            }

            R.id.nav_settings -> {
                callSettingsActivity()
            }

            R.id.nav_singOut -> {
                signOut()
            }
        }
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun callSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)

    }

    private fun callAccountActivity() {
        val intent = Intent(this, AccountActivity::class.java)
        startActivity(intent)
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas cerrar la aplicación?").setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, id ->
                finishAffinity()
            }.setNegativeButton("Cancelar") { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }


    fun yearProgressCalculation(): Double {
        val today = Calendar.getInstance()
        val hourOfDay = today.get(Calendar.HOUR_OF_DAY)
        val dayOfYear = today.get(Calendar.DAY_OF_YEAR)
        val year = today.get(Calendar.YEAR)

        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
        val totalHoursInYear = if (isLeapYear) 365 * 24 else 366 * 24

        val totalHourPassed = (dayOfYear - 1) * 24 + hourOfDay

        return (totalHourPassed.toDouble() / totalHoursInYear) * 100

    }

    object AppTheme {
        val RED = AppThemeRed
        val GREEN = AppThemeGreen
        val BLUE = AppThemeBlue
        val ORANGE = AppThemeOrange
        val BLACK = AppThemeBlack
        val DEFAULT = Base_Theme_DailyTracker
    }
}

