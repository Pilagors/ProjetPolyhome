package com.monfort.projetpolyhome

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.adapters.HouseAdapter
import com.monfort.projetpolyhome.data.HouseItemData

class HousesActivity : AppCompatActivity() {
    val housesInfos : ArrayList<HouseItemData> = ArrayList()
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
    }

    // recuperer les maisons
    private fun fetchHouses () {

    }

    // recuperer les membres de la maison
    private fun fetchHouseMembers() {

    }

    // -> trouver le owner pour chaque maison pour les associer dans le HouseItemData

    private fun initListHouses() {
        findViewById<ListView>(R.id.listHomes).adapter = adapter
    }
}