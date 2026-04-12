package com.monfort.projetpolyhome

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.monfort.projetpolyhome.adapters.DeviceAdapter
import com.monfort.projetpolyhome.data.CommandData
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.data.DevicesResponse
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.CommandManager
import com.monfort.projetpolyhome.utils.HouseManager
import com.monfort.projetpolyhome.utils.TokenManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var webView : WebView
    lateinit var adapter : DeviceAdapter
    val devicesList : ArrayList<DeviceData> = ArrayList()
    val commandManager = CommandManager()
    private var poll: Job? = null
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var safeModeContainer: LinearLayout
    private lateinit var webViewContainer: FrameLayout
    private var isWebViewEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.webView = findViewById<WebView>(R.id.houseView)
        this.loadingSpinner = findViewById(R.id.loadingSpinner)
        this.safeModeContainer = findViewById(R.id.safeModeContainer)
        this.webViewContainer = findViewById(R.id.webViewContainer)
        this.adapter = DeviceAdapter(this, devicesList, ::deviceButtonCommand)

        val listView = findViewById<ExpandableListView>(R.id.middleListView)
        listView.setAdapter(adapter)

        initSafeMode()
    }

    private fun initSafeMode() {
        val btnEnable = findViewById<MaterialButton>(R.id.btnEnableWebView)
        val btnDisable = findViewById<MaterialButton>(R.id.btnDisableWebView)
        val textExternalLink = findViewById<TextView>(R.id.textExternalLink)

        btnEnable.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Activation de la vue 3D")
                .setMessage("L'affichage de la maison en 3D est gourmand en ressources et peut ralentir ou faire crasher l'application sur certains appareils. Voulez-vous continuer ?")
                .setPositiveButton("Accepter") { _, _ ->
                    isWebViewEnabled = true
                    safeModeContainer.visibility = View.GONE
                    webViewContainer.visibility = View.VISIBLE
                    loadWebView(HouseManager(this).getHouseId())
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        btnDisable.setOnClickListener {
            isWebViewEnabled = false
            webViewContainer.visibility = View.GONE
            safeModeContainer.visibility = View.VISIBLE
            webView.loadUrl("about:blank")
        }

        textExternalLink.setOnClickListener {
            val houseId = HouseManager(this).getHouseId()
            if (houseId != -1) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://polyhome.lesmoulinsdudev.com/?houseId=$houseId"))
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val houseManager = HouseManager(this)
        val houseId = houseManager.getHouseId()

        if (houseManager.hasHouseIdChanged()) {
            resetUIForNewHouse()
        } else if (webView.url == null && isWebViewEnabled) {
            loadWebView(houseId)
        }
        
        refreshHouseIdView(houseId)
        
        if (poll == null || !poll!!.isActive) {
            getDevicesListAsync(houseId)
        }
    }

    override fun onPause() {
        super.onPause()
        poll?.cancel()
    }

    private fun resetUIForNewHouse() {
        loadingSpinner.visibility = View.VISIBLE
        devicesList.clear()
        commandManager.update(emptyList())
        adapter.update(devicesList)
        
        isWebViewEnabled = false
        webViewContainer.visibility = View.GONE
        webView.loadUrl("about:blank")
        safeModeContainer.visibility = View.VISIBLE
    }

    private fun loadWebView(houseId: Int) {
        if (houseId == -1) return
        
        webView.setBackgroundColor(Color.TRANSPARENT)
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                val css = ".controls { display: none !important; }"
                val js = "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                webView.evaluateJavascript(js, null)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.postDelayed({
                    val disableShadowsJS = """
                        (function() {
                            var check = document.getElementById('chkDisableShadows');
                            if (check && !check.checked) { check.click(); }
                        })();
                    """.trimIndent()
                    webView.evaluateJavascript(disableShadowsJS, null)
                }, 1000)
            }
        }

        webView.loadUrl("https://polyhome.lesmoulinsdudev.com/?houseId=${houseId}")
    }

    private fun refreshHouseIdView(houseId: Int) {
        val houseIdView = findViewById<TextView>(R.id.houseIdView)
        if (houseId != -1) {
            houseIdView.text = "Polyhome $houseId"
        } else {
            houseIdView.text = "Polyhome inconnue"
        }
    }

    private fun getDevicesListAsync(houseId: Int, delay: Long = 0) {
        poll?.cancel()
        poll = lifecycleScope.launch {
            delay(delay)
            while (isActive) {
                getDevicesList(houseId)
                delay(3000)
            }
        }
    }

    private fun getDevicesList(houseId: Int) {
        if (houseId == -1) return
        val token = TokenManager(this).getToken()
        Api().get<DevicesResponse>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::successDevicesList,
            token
        )
    }

    private fun successDevicesList(responseCode: Int, response: DevicesResponse?) {
        runOnUiThread {
            when (responseCode) {
                200 -> updateDevices(response?.devices ?: emptyList())
                403 -> {
                    HouseManager(this).logout()
                    val intent = Intent(this, HousesActivity::class.java)
                    startActivity(intent)
                }
                500 -> {
                    poll?.cancel()
                    getDevicesListAsync(HouseManager(this).getHouseId(), 10000)
                }
            }
        }
    }

    fun deviceButtonCommand(device: DeviceData, command: String) {
        val token = TokenManager(this).getToken()
        val houseId = HouseManager(this).getHouseId()
        Api().post<CommandData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/${device.id}/command",
            CommandData(command),
            ::successButtonCommand,
            token
        )
    }

    private fun successButtonCommand(responseCode: Int) {
        val houseId = HouseManager(this).getHouseId()
        getDevicesList(houseId)
    }

    private fun updateDevices(devices: List<DeviceData>) {
        if (devices.isNotEmpty()) {
            loadingSpinner.visibility = View.GONE
        }
        devicesList.clear()
        devicesList.addAll(devices)
        commandManager.update(devices)
        adapter.update(devicesList)
    }

    fun goToHouses(view: View) {
        val intent = Intent(this, HousesActivity::class.java)
        startActivity(intent)
    }

    fun goToUserActivity(view: View){
        val intent = Intent(this, UsersActivity::class.java)
        startActivity(intent)
    }

    fun logout(view: View) {
        poll?.cancel()
        TokenManager(this).logout()
        HouseManager(this).logout()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}