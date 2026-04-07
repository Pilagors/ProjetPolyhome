package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.HouseData
import com.monfort.projetpolyhome.utils.HouseManager

class HouseAdapter(
    private val context : Context,
    private val dataSource : ArrayList<HouseData>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): HouseData? {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val rowView = inflater.inflate(R.layout.houses_list_item, parent, false)

        val current = getItem(position)

        rowView.findViewById<TextView>(R.id.houseNumber).text = "${current?.houseId}"
        rowView.findViewById<TextView>(R.id.isOwner).visibility = if(current?.owner == true) View.VISIBLE else View.INVISIBLE

        // Changement visiblité si selectionné
        val houseId = HouseManager(context).getHouseId()

        if (current?.houseId == houseId) {
            rowView.setBackgroundColor(Color.parseColor("#D3E3FC"))
        } else {
            rowView.setBackgroundColor(Color.TRANSPARENT)
        }

        // onClickListener
        rowView.setOnClickListener {
            current?.let {
                HouseManager(context).saveHouseId(it.houseId)
                HouseManager(context).saveOwner(it.owner)
                notifyDataSetChanged()
            }
        }

        return rowView
    }

}