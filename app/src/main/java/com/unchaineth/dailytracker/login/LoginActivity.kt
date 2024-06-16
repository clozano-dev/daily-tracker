package com.unchaineth.dailytracker.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.unchaineth.dailytracker.main.MainActivity
import com.unchaineth.dailytracker.R
import com.unchaineth.dailytracker.settings.ThemePreferences
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    private lateinit var themePreferences: ThemePreferences

    companion object {
        lateinit var userEmail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var lyTerms: LinearLayout

    private lateinit var mAuth: FirebaseAuth

    private var RESULT_CODE_GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)

        setTheme(themePreferences.getSavedTheme())

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        lyTerms = findViewById(R.id.lyTerms)
        lyTerms.visibility = View.INVISIBLE
        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        mAuth = FirebaseAuth.getInstance()

        manageButtonLogin()
        etEmail.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
        etPass.doOnTextChanged { text, start, before, count -> manageButtonLogin() }

    }

    private fun manageButtonLogin() {
        var btLogin = findViewById<TextView>(R.id.btLogin)
        email = etEmail.text.toString()
        password = etPass.text.toString()

        if (TextUtils.isEmpty(password) || !ValidateEmail.isEmail(email)) {
            btLogin.isEnabled = false
        } else {
            btLogin.isEnabled = true
        }
    }

    fun login(view: View) {
        loginUser()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            goHome(currentUser.email.toString(), currentUser.providerId)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPass.text.toString()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) goHome(email, "email") else {
                    if (lyTerms.visibility == View.INVISIBLE) {
                        lyTerms.visibility = View.VISIBLE
                        Toast.makeText(
                            this,
                            "Acepta los términos y condiciones de uso para registrarte",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        var cbAccept = findViewById<CheckBox>(R.id.cbAccept)
                        if (cbAccept.isChecked) register()
                    }
                }
            }
    }

    private fun goHome(email: String, provider: String) {
        userEmail = email
        providerSession = provider
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun register() {
        email = etEmail.text.toString()
        password = etPass.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    registerDataBase()
                    goHome(email, "email")
                } else Toast.makeText(
                    this,
                    "Ha ocurrido un error al crear el usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun goTerms(v: View) {
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)
    }

    fun forgotPass(view: View) {
        resetPassword()
    }

    private fun resetPassword() {
        var e = etEmail.text.toString()
        if (!TextUtils.isEmpty(e)) {
            mAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Toast.makeText(
                        this,
                        "Email enviado a $e",
                        Toast.LENGTH_LONG
                    ).show()
                    else Toast.makeText(
                        this,
                        "No se ha encontrado usuario con ese correo electrónico",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else Toast.makeText(
            this,
            "Indica un correo electrónico",
            Toast.LENGTH_LONG
        ).show()
    }

    fun callLoginGoogle(view: View) {
        loginGoogle()
    }

    private fun loginGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!

                if (account != null) {
                    email = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                        registerDataBase()
                        goHome(email, "Google")}
                        else Toast.makeText(
                        this,
                        "Error en la conexión con Google",
                        Toast.LENGTH_LONG
                    ).show()
                    }
                }

            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerDataBase(){
        var dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
        var dbRegister = FirebaseFirestore.getInstance()
        dbRegister.collection("users").document(email)
            .set(
                hashMapOf(
                    "user" to email,
                    "dateRegister" to dateRegister
                )
            )
    }
}