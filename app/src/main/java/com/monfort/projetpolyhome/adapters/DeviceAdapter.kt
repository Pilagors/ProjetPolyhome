package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.DeviceData

class DeviceAdapter(
    val context: Context,
    val devices: List<DeviceData>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): DeviceData? {
        return devices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val rowView = inflater.inflate(R.layout.home_list_item, parent, false)
        val device = getItem(position)

        val deviceIdText = rowView.findViewById<TextView>(R.id.deviceIdText)
        deviceIdText.text = device?.id

        val deviceTypeText = rowView.findViewById<TextView>(R.id.deviceTypeText)
        deviceTypeText.text = device?.type

        return rowView
    }
}