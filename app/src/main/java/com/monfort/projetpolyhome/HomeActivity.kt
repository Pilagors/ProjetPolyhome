package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.utils.HouseIdManager
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

    }

    override fun onResume() {
        super.onResume()

        val houseId = HouseIdManager(this).getHouseId()

        viewHouse(houseId)
        refreshHouseIdView(houseId)
    }

    private fun viewHouse(houseId: Int) {
        val webView = findViewById<WebView>(R.id.houseView)

        if (houseId == -1) {
            webView.visibility = View.INVISIBLE

        } else {

            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true

            webView.webViewClient = WebViewClient()

            webView.loadUrl("https://polyhome.lesmoulinsdudev.com/?houseId=${houseId}")
        }
    }

    private fun refreshHouseIdView(houseId: Int) {
        val houseIdView = findViewById<TextView>(R.id.houseIdView)
        if (houseId != -1) {
            houseIdView.text = "Polyhome $houseId"
        } else {
            houseIdView.text = "Polyhome inconnue"
        }

    }

    fun goToHouses(view: View) {
        val intent = Intent(this, HousesActivity::class.java)
        startActivity(intent)
    }

    fun logout(view: View) {
        TokenManager(this).logout()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}