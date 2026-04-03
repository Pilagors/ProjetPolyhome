package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.DeviceData

class DeviceAdapter(
    val context: Context,
    val devices: List<DeviceData>,
    val command: (device: DeviceData, command: String) -> Unit
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
            val commandView = inflater.inflate(R.layout.commands_buttons, devicesContainer, false)
            commandView.findViewById<TextView>(R.id.deviceId).text = device.id

            val btnLayout = commandView.findViewById<LinearLayout>(R.id.btnLayout)

            val btnMap : MutableMap<String, Button> = mutableMapOf()

            for (command in device.availableCommands) {
                val btn = Button(context).apply {
                    text = command
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    setOnClickListener { command(device, command) }
                }
                btnLayout.addView(btn)
                btnMap[command] = btn
            }
            customButtonVisibility(device, btnMap)

            devicesContainer.addView(commandView)
        }

        return rowView
    }

    fun update(newDevices: List<DeviceData>) {
        groups = newDevices.groupBy { it.type }.entries.toList()
        notifyDataSetChanged()
    }

    fun customButtonVisibility(device: DeviceData, btnMap: Map<String, Button>) {
        val opening = device.opening
        val openingMode = device.openingMode
        val power = device.power

        if (power != null) {
            when {
                power == 0 -> btnMap["TURN OFF"]?.visibility = View.GONE
                power == 1 -> btnMap["TURN ON"]?.visibility = View.GONE
            }
        }

        if (opening != null) {
            when {
                openingMode == 0 -> {
                    btnMap["OPEN"]?.visibility = View.GONE
                }
                openingMode == 1 -> {
                    btnMap["CLOSE"]?.visibility = View.GONE
                }
            }
        }
    }

}