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
import com.monfort.projetpolyhome.data.UserRequest
import com.monfort.projetpolyhome.data.UserData
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.HouseManager
import com.monfort.projetpolyhome.utils.TokenManager

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

        adapter = UserAdapter(this,usersInfos) { user ->
            deleteUser(user)
        }
        initButton()
        fetchUsers()
        initOwnerButton()
        initListUsers()
    }


    private fun initButton(){
        findViewById<Button>(R.id.goToHomeActivityFromUsers).setOnClickListener { backToHome() }
    }

    private fun backToHome(){
        finish()
    }

    private fun initListUsers(){
        findViewById<ListView>(R.id.listUsers).adapter = adapter
    }

    private fun fetchUsers(){
        val token = TokenManager(this).getToken()
        val pickedHouse = HouseManager(this).getHouseId()
        if (pickedHouse != -1){
            Api().get<List<UserData>>("https://polyhome.lesmoulinsdudev.com/api/houses/${pickedHouse}/users",::successFetchUsers,token)
        }
    }

    private fun successFetchUsers(responseCode : Int, response : List<UserData>?){
        runOnUiThread {
            when(responseCode) {
                200 -> {
                    if (response != null) {
                        usersInfos.clear()
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
        val add = findViewById<Button>(R.id.AddUsersFromHouseButton)
        if (HouseManager(this).isOwner()){
            add.visibility = View.VISIBLE
            add.setOnClickListener {
                showAddUserDialog()
            }
        }
        else {
            add.visibility = View.INVISIBLE
        }
    }

    private fun addUsers(user: String){
        val token = TokenManager(this).getToken()
        val pickedHouse = HouseManager(this).getHouseId()
        if (pickedHouse != -1){

            val dataUser = UserRequest(user)
            Api().post<UserRequest, UserData>("https://polyhome.lesmoulinsdudev.com/api/houses/${pickedHouse}/users",dataUser,::successAddUser,token)
        }
    }

    private fun successAddUser(responseCode : Int, response : UserData?){
        runOnUiThread {
            when(responseCode) {
                200,201 -> {
                    fetchUsers()
                    Toast.makeText(this,"user added",Toast.LENGTH_SHORT).show()
                }
                404 -> {
                    Toast.makeText(this,"User not found", Toast.LENGTH_SHORT).show()
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

    private fun deleteUser(user: String){
        val token = TokenManager(this).getToken()
        val pickedHouse = HouseManager(this).getHouseId()
        if (pickedHouse != -1){

            val dataUser = UserRequest(user)
            Api().delete("https://polyhome.lesmoulinsdudev.com/api/houses/${pickedHouse}/users",dataUser,::successDeleteUser,token)
        }
    }

    private fun successDeleteUser(responseCode : Int){
        runOnUiThread {
            when(responseCode) {
                200 -> {
                    Toast.makeText(this,"user deleted",Toast.LENGTH_SHORT).show()
                    fetchUsers()
                }
                404 -> {
                    Toast.makeText(this,"User not found", Toast.LENGTH_SHORT).show()
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

    private fun showAddUserDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Ajouter un utilisateur")
        builder.setMessage("Entrez l'identifiant du nouvel utilisateur :")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Ajouter") { dialog, _ ->
            val userInput = input.text.toString().trim()

            if (userInput.isNotEmpty()) {
                addUsers(userInput)
            } else {
                Toast.makeText(this, "Le champ ne peut pas être vide", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}

