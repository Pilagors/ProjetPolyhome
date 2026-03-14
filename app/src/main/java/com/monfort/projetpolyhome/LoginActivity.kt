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
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.data.LoginData
import com.monfort.projetpolyhome.data.LoginResponse
import com.monfort.projetpolyhome.utils.TokenManager

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

    private fun loginSuccess(responseCode : Int, response : LoginResponse?) {
        runOnUiThread {
            when (responseCode) {
                200 -> { // succes
                    val token = response?.token

                    if (!token.isNullOrEmpty()) {
                        val tokenManager = TokenManager(this)
                        tokenManager.saveToken(token)

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        finish()
                    }
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

        Api().post<LoginData, LoginResponse>("https://polyhome.lesmoulinsdudev.com/api/users/auth", data, ::loginSuccess)
    }
}