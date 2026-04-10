package com.monfort.projetpolyhome

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.adapters.HouseAdapter
import com.monfort.projetpolyhome.data.HouseData
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.TokenManager

class HousesActivity : AppCompatActivity() {
    val housesInfos : ArrayList<HouseData> = ArrayList()
    lateinit var adapter : HouseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_houses)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = HouseAdapter(this, housesInfos)
        initListHouses()

        fetchHouses()
    }
    private fun fetchHouses () {
        val token = TokenManager(this).getToken()

        Api().get<List<HouseData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::successFetchHouses, token)
    }

    private fun successFetchHouses(responseCode : Int, response : List<HouseData>?) {
        runOnUiThread {
            when(responseCode) {
                200 -> {
                    if (!response.isNullOrEmpty()) {
                        housesInfos.addAll(response)

                        adapter.notifyDataSetChanged()
                    }
                }
                403 -> {
                    Toast.makeText(this,"Accès refusé",Toast.LENGTH_SHORT).show()
                }
                500 -> {
                    Toast.makeText(this,"erreur serveur",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initListHouses() {
        findViewById<ListView>(R.id.listHomes).adapter = adapter
    }
}