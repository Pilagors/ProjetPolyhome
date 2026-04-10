package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.UserData
import com.monfort.projetpolyhome.utils.HouseManager
import com.monfort.projetpolyhome.utils.TokenManager

class UserAdapter(
    private val context: Context,
    private val dataSource: ArrayList<UserData>,
    private val onDeleteClick : (String) -> Unit
): BaseAdapter(){

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val rowView = convertView ?: inflater.inflate(R.layout.users_list_item,parent,false)
        val user = getItem(position) as UserData
        rowView.findViewById<TextView>(R.id.userName).text = user.userLogin
        val ownerText = rowView.findViewById<TextView>(R.id.userOwner)
        val delete = rowView.findViewById<Button>(R.id.deleteButton)
        if (user.owner == 1){
            ownerText.text = "Propriétaire"
        }
        else {
            ownerText.text = ""
        }

        val isCurrentUserOwner = HouseManager(context).isOwner()

        if (isCurrentUserOwner && user.owner !=1 ){
            delete.visibility = View.VISIBLE
            delete.setOnClickListener {
                onDeleteClick(user.userLogin)
            }
        }
        else {
            delete.visibility = View.INVISIBLE
            delete.setOnClickListener(null)
        }

        return rowView
    }
}