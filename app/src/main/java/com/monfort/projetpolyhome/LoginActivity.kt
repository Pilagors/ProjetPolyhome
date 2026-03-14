package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api
import com.monfort.projetpolyhome.data.LoginData

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initLoginButton()
    }

    private fun initLoginButton() {
        val button = findViewById<Button>(R.id.btnLogin)

        button.setOnClickListener {
            login(it)
        }
    }

    private fun loginSuccess(responseCode : Int, token : String?) {
        runOnUiThread {
            when (responseCode) {
                200 -> { // succes
                    Log.d("success", "oui")
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                }
                400 -> { // data incorrect
                    Toast.makeText(this,"données incorrectes",Toast.LENGTH_SHORT).show()
                }
                404 -> { // no user found
                    Toast.makeText(this,"utilisateur introuvable",Toast.LENGTH_SHORT).show()
                }
                500 -> { // erreur serveur
                    Toast.makeText(this,"erreur serveur",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun login(view: View) {
        val usernameText = findViewById<EditText>(R.id.txtUsername)
        val passwordText = findViewById<EditText>(R.id.txtPassword)

        val data = LoginData(login = usernameText.text.toString(), password = passwordText.text.toString())

        Api().post<LoginData, String>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess)
    }
}