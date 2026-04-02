package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.adapters.DeviceAdapter
import com.monfort.projetpolyhome.components.CustomWebView
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.utils.HouseIdManager
import com.monfort.projetpolyhome.utils.TokenManager

class HomeActivity : AppCompatActivity() {

    private lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initList()
    }

    override fun onResume() {
        super.onResume()

        val houseId = HouseIdManager(this).getHouseId()

        viewHouse(houseId, webView)
        refreshHouseIdView(houseId)
        initDevicesCards(houseId)
    }

    private fun initList() {
        val listView = findViewById<ListView>(R.id.middleListView)
        val header = layoutInflater.inflate(R.layout.header_home_list_view, listView, false)
        this.webView = header.findViewById<WebView>(R.id.houseView)
        listView.addHeaderView(header)
        listView.adapter = DeviceAdapter(this, emptyList())
    }

    private fun viewHouse(houseId: Int, webView: WebView) {
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

    private fun initDevicesCards(houseId: Int) {
        if (houseId == -1) {
            return
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