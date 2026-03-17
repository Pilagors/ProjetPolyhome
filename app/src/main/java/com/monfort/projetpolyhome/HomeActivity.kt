package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.utils.TokenManager

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUsersButton()
        initLogoutButton()
    }

    fun goToHouses(view: View) {
        val intent = Intent(this, HousesActivity::class.java)
        startActivity(intent)
    }

    private fun initLogoutButton() {
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener { logout() }
    }

    private fun initUsersButton(){
        findViewById<Button>(R.id.goToUserActivityFromHome).setOnClickListener { goToUserActivity() }
    }

    private fun goToUserActivity(){
        val intent = Intent(this, UsersActivity::class.java)
        startActivity(intent)
    }
    private fun logout() {
        TokenManager(this).logout()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}