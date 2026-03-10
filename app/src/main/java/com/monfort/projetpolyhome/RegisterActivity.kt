package com.monfort.projetpolyhome

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

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

        val data = RegisterData(name.text.toString(),password.text.toString())
        Api().post<RegisterData>("https://polyhome.lesmoulinsdudev.com/api/users/register",data,::registerSucces)
    }

    private fun registerSucces(reponseCode :Int){
        when (reponseCode) {
            200 -> {
                Toast.makeText(this, "Le compte a bien été créé", Toast.LENGTH_SHORT).show()
            }
            400 -> {
                Toast.makeText(this, "Erreur : Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
            }
            409 -> {
                Toast.makeText(this, "Erreur : Ce login est déjà utilisé", Toast.LENGTH_LONG).show()
            }
            500 -> {
                Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Erreur inconnue (Code: $reponseCode)", Toast.LENGTH_LONG).show()
            }
        }
    }
}