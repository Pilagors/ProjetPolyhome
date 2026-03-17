package com.monfort.projetpolyhome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.monfort.projetpolyhome.adapters.UserAdapter
import com.monfort.projetpolyhome.data.UserData
import com.monfort.projetpolyhome.utils.Api

class UsersActivity : AppCompatActivity() {
    val usersInfos : ArrayList<UserData> = ArrayList()
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
        initButton()
        initListUsers()
        adapter = UserAdapter(this,usersInfos)
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

    private fun getUsersInMyHouse(){
    }


}