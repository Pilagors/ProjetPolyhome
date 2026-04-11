package com.monfort.projetpolyhome.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.monfort.projetpolyhome.R
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.monfort.projetpolyhome.data.DeviceData

class ExpandableDeviceAdapter (
    val context: Context,
    val sendCommand: (DeviceData, String) -> Unit
) : BaseExpandableListAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var groups: List<String> = emptyList()
    private var children: Map<String, List<DeviceData>> = emptyMap()

    fun update(devices: List<DeviceData>) {
        groups = devices.map { it.type }.distinct()
        children = devices.groupBy { it.type }
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int {
        return groups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return children[groups[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any? {
        return groups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        return children[groups[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view = inflater.inflate(R.layout.home_list_item, parent, false)

        view.findViewById<TextView>(R.id.deviceTypeText).text = groups[groupPosition]
        view.findViewById<LinearLayout>(R.id.devicesContainer).removeAllViews()

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view = inflater.inflate(R.layout.commands_buttons, parent, false)
        val device = getChild(groupPosition, childPosition) as DeviceData

        view.findViewById<TextView>(R.id.deviceId).text = device.id

        val btnLayout = view.findViewById<LinearLayout>(R.id.btnLayout)
        btnLayout.removeAllViews()

        for (command in device.availableCommands) {
            val btn = Button(context).apply {
                text = command
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener { sendCommand(device, command) }
            }
            btnLayout.addView(btn)
        }

        return view
    }
}