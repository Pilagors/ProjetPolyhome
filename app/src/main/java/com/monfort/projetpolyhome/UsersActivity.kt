package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.adapters.UserAdapter
import com.monfort.projetpolyhome.data.HouseData
import com.monfort.projetpolyhome.data.UserData
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.HouseIdManager
import com.monfort.projetpolyhome.utils.TokenManager

class UsersActivity : AppCompatActivity() {
    val usersInfos : ArrayList<UserData> = ArrayList()
    val housesInfos : ArrayList<HouseData> = ArrayList()

    lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = UserAdapter(this,usersInfos)
        initButton()
        fetchUsers()
        initOwnerButton()
        initListUsers()
    }


    private fun initButton(){
        findViewById<Button>(R.id.goToHomeActivityFromUsers).setOnClickListener { backToHome() }
    }

    private fun backToHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun initListUsers(){
        findViewById<ListView>(R.id.listUsers).adapter = adapter
    }

    private fun fetchUsers(){
        val token = TokenManager(this).getToken()
        val pickedHouse = HouseIdManager(this).getHouseId()
        if (pickedHouse != -1){
            Api().get<List<UserData>>("https://polyhome.lesmoulinsdudev.com/api/houses/${pickedHouse}/users",::successFetchUsers,token)

        }
    }

    private fun successFetchUsers(responseCode : Int, response : List<UserData>?){
        runOnUiThread {
            when(responseCode) {
                200 -> {
                    if (!response.isNullOrEmpty()) {
                        usersInfos.addAll(response)
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

    private fun initOwnerButton(){
        val caca = findViewById<Button>(R.id.AddUsersFromHouseButton)
        val pipi = findViewById<Button>(R.id.DeleteUsersFromHouseButton)
        if (HouseIdManager(this).isOwner()){
            pipi.visibility = View.VISIBLE
            caca.visibility = View.VISIBLE
        }
        else {
            pipi.visibility = View.INVISIBLE
            caca.visibility = View.INVISIBLE
        }
    }
}