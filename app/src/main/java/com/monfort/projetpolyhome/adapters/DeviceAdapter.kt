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
import com.monfort.projetpolyhome.data.CommandData
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.utils.Api
import com.monfort.projetpolyhome.utils.HouseIdManager
import com.monfort.projetpolyhome.utils.TokenManager
import org.w3c.dom.Text

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
            val commandView = inflater.inflate(R.layout.commands_buttons, devicesContainer, false)
            commandView.findViewById<TextView>(R.id.deviceId).text = device.id

            val btnLayout = commandView.findViewById<LinearLayout>(R.id.btnLayout)
            commandView.findViewById<TextView>(R.id.deviceId).text = device.id

            for (command in device.availableCommands) {
                val btn = Button(context).apply {
                    text = command
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    setOnClickListener { buttonCommand(device, command) }
                }
                btnLayout.addView(btn)
            }

            devicesContainer.addView(commandView)
        }

        return rowView
    }

    fun update(newDevices: List<DeviceData>) {
        groups = newDevices.groupBy { it.type }.entries.toList()
        notifyDataSetChanged()
    }

    fun buttonCommand(device: DeviceData, command: String) {
        val token = TokenManager(context).getToken()
        val houseId = HouseIdManager(context).getHouseId()

        Api().post<CommandData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/${device.id}/command",
            CommandData(command),
            ::successButtonCommand,
            token)

    }

    private fun successButtonCommand(responseCode: Int) {

    }
}