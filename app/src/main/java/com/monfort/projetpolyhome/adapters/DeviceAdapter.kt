package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.DeviceData

class DeviceAdapter(
    val context: Context,
    val devices: List<DeviceData>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    //     key        value
    // map<type, liste_de_controles>
    private var groups: List<Map.Entry<String, List<DeviceData>>> = devices.groupBy { it.type }.entries.toList()

    override fun getCount(): Int {
        return groups.size
    }

    override fun getItem(position: Int): Any? {
        return groups[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val rowView = inflater.inflate(R.layout.home_list_item, parent, false)

        val group = groups[position]
        val devices = group.value

        val devicesContainer = rowView.findViewById<LinearLayout>(R.id.devicesContainer)
        val deviceTypeText = rowView.findViewById<TextView>(R.id.deviceTypeText)

        deviceTypeText.text = group.key
        devicesContainer.removeAllViews()

        for (device in devices) {
            val row = TextView(context).apply {
                text = device.id
                textSize = 16f
            }

            devicesContainer.addView(row)
        }

        return rowView
    }

    fun update(newDevices: List<DeviceData>) {
        groups = newDevices.groupBy { it.type }.entries.toList()
        notifyDataSetChanged()
    }
}