package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.data.RegisterData

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onClickRegisterButton()
    }

    private fun onClickRegisterButton(){
        val btn = findViewById<Button>(R.id.buttonRegister)
        btn.setOnClickListener { register() }
    }

    private fun register(){
        val name = findViewById<EditText>(R.id.usernameRegister)
        val password = findViewById<EditText>(R.id.passwordRegister)

        val data = RegisterData(name.text.toString(), password.text.toString())
        Api().post<RegisterData>("https://polyhome.lesmoulinsdudev.com/api/users/register",data,::registerSucces)
    }

    private fun registerSucces(reponseCode :Int){
        runOnUiThread(){
            when (reponseCode) {
                200 -> {
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }
                400 -> {
                    Toast.makeText(this,"données incorrectes",Toast.LENGTH_SHORT).show()
                }
                409 -> {
                    Toast.makeText(this,"login déjà utilisé",Toast.LENGTH_SHORT).show()
                }
                500 -> {
                    Toast.makeText(this,"erreur serveur", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this,"erreur inconnue", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}