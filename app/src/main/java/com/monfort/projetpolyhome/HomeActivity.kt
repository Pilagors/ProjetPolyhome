package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.monfort.projetpolyhome.adapters.DeviceAdapter
import com.monfort.projetpolyhome.data.CommandData
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.data.DevicesResponse
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.HouseManager
import com.monfort.projetpolyhome.utils.TokenManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    lateinit var adapter: DeviceAdapter
    val devicesList: ArrayList<DeviceData> = ArrayList()

    private var poll: Job? = null

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

        this.adapter = DeviceAdapter(this, devicesList, ::deviceButtonCommand)

        initList()
    }

    override fun onResume() {
        super.onResume()

        val houseManager = HouseManager(this)
        val houseId = houseManager.getHouseId()

        if (houseManager.hasHouseIdChanged() || webView.url == null) {
            viewHouse(houseId, webView)
        }
        refreshHouseIdView(houseId)
    }

    override fun onPause() {
        super.onPause()
        poll?.cancel()
    }

    private fun initList() {
        val listView = findViewById<ListView>(R.id.middleListView)
        listView.adapter = adapter
    }

    private fun viewHouse(houseId: Int, webView: WebView) {
        if (houseId == -1) {
            webView.visibility = View.INVISIBLE

        } else {
            webView.visibility = View.VISIBLE

            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    webView.postDelayed({
                        val js = """
                            (function() {
                                const div = document.getElementsByClassName('controls')[0];
                                const check = document.getElementById('chkDisableShadows');
                                if (check && div) {
                                    div.style.display = 'none';
                                    if (check.checked === false) {
                                        check.click();
                                    }
                                }
                            })();
                             
                        """.trimIndent()

                        webView.evaluateJavascript(js, null)

                        getDevicesListAsync(houseId)
                    }, 500)
                }
            }

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

    private fun getDevicesListAsync(houseId: Int, delay: Long = 0) {
        poll?.cancel()
        poll = lifecycleScope.launch {
            delay(delay)
            while (isActive) {
                getDevicesList(houseId)
                delay(1000)
            }
        }
    }

    private fun getDevicesList(houseId: Int) {
        if (houseId == -1) {
            return
        }
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
                200 -> {
                    initListCards(response?.devices ?: emptyList())
                }

                400 -> {
                    Toast.makeText(this, "Données fournies incorrectes", Toast.LENGTH_SHORT).show()
                }

                403 -> {
                    Toast.makeText(this, "Accès refusé", Toast.LENGTH_SHORT).show()
                }

                500 -> {
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show()
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

    private fun initListCards(devices: List<DeviceData>) {
        devicesList.clear()
        devicesList.addAll(devices)
        adapter.update(devicesList)

    }

    fun goToHouses(view: View) {
        val intent = Intent(this, HousesActivity::class.java)
        startActivity(intent)
    }

    fun goToUserActivity(view: View) {
        val intent = Intent(this, UsersActivity::class.java)
        startActivity(intent)
    }

    fun logout(view: View) {
        TokenManager(this).logout()
        HouseManager(this).logout()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}