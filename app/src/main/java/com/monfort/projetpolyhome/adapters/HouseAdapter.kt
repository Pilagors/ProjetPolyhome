package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.HouseItemData
import com.monfort.projetpolyhome.data.UserData
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.TokenManager

class HouseAdapter(
    private val context : Context,
    private val dataSource : ArrayList<HouseItemData>
) : BaseAdapter() {

    lateinit var houseUsers : List<UserData>

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): HouseItemData? {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val rowView = inflater.inflate(R.layout.houses_list_item, parent, false)
        val token = TokenManager(context).getToken() ?: ""

        val current = getItem(position)

        rowView.findViewById<TextView>(R.id.houseNumber).text = "${current?.id}"
        rowView.findViewById<TextView>(R.id.houseOwner).text = "${current?.owner}"

        return rowView
    }
}