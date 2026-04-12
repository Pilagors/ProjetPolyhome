package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.components.GroupCommandPanel
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.utils.CommandManager

class DeviceAdapter(
    private val context: Context,
    private var devices: List<DeviceData>,
    private val command: (device: DeviceData, command: String) -> Unit
) : BaseExpandableListAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val commandManager = CommandManager(devices)
    
    private var types: List<String> = emptyList()
    private var devicesByType: Map<String, List<DeviceData>> = emptyMap()

    init {
        updateData(devices)
    }

    private fun updateData(newDevices: List<DeviceData>) {
        devices = newDevices
        commandManager.update(newDevices)
        
        types = commandManager.getTypes()
        devicesByType = types.associateWith { type ->
            commandManager.filterDevices(type = type)
        }
    }

    fun update(newDevices: List<DeviceData>) {
        updateData(newDevices)
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int = types.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val count = devicesByType[types[groupPosition]]?.size ?: 0
        // On ajoute le panneau de commande groupée seulement s'il y a plus d'un appareil
        return if (count > 1) count + 1 else count
    }

    override fun getGroup(groupPosition: Int): Any = types[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        val typeDevices = devicesByType[types[groupPosition]] ?: return null
        return if (typeDevices.size > 1) {
            if (childPosition == 0) null else typeDevices[childPosition - 1]
        } else {
            typeDevices[childPosition]
        }
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.device_group_header, parent, false)
        val type = types[groupPosition]
        val titleView = view.findViewById<TextView>(R.id.groupTitle)
        val indicator = view.findViewById<ImageView>(R.id.indicator)

        titleView.text = type.replaceFirstChar { it.uppercase() }
        indicator.setImageResource(if (isExpanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float)
        
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val type = types[groupPosition]
        val typeDevices = devicesByType[type]!!

        if (typeDevices.size > 1 && childPosition == 0) {
            // Afficher le GroupCommandPanel comme premier enfant seulement si plusieurs devices
            val panel = GroupCommandPanel(context)
            panel.bind(type, commandManager, command)
            panel.setPadding(32, 16, 32, 16)
            return panel
        }

        // Déterminer l'index réel du device
        val deviceIndex = if (typeDevices.size > 1) childPosition - 1 else childPosition
        val device = typeDevices[deviceIndex]

        val view = inflater.inflate(R.layout.commands_buttons, parent, false)
        val deviceIdText = view.findViewById<TextView>(R.id.deviceId)
        val btnLayout = view.findViewById<LinearLayout>(R.id.btnLayout)
        
        deviceIdText.text = device.id
        
        // Nettoyage et création des boutons
        val viewsToRemove = mutableListOf<View>()
        for (i in 0 until btnLayout.childCount) {
            val child = btnLayout.getChildAt(i)
            if (child is Button) viewsToRemove.add(child)
        }
        viewsToRemove.forEach { btnLayout.removeView(it) }

        val btnMap: MutableMap<String, Button> = mutableMapOf()
        for (cmd in device.availableCommands) {
            val btn = Button(context).apply {
                text = cmd
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener { command(device, cmd) }
            }
            btnLayout.addView(btn)
            btnMap[cmd] = btn
        }

        customButtonVisibility(device, btnMap)
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    private fun customButtonVisibility(device: DeviceData, btnMap: Map<String, Button>) {
        val openingMode = device.openingMode
        val power = device.power

        if (power != null) {
            when (power) {
                0 -> btnMap["TURN OFF"]?.visibility = View.GONE
                1 -> btnMap["TURN ON"]?.visibility = View.GONE
            }
        }

        if (openingMode != null) {
            when (openingMode) {
                0 -> btnMap["OPEN"]?.visibility = View.GONE
                1 -> btnMap["CLOSE"]?.visibility = View.GONE
            }
        }
    }
}