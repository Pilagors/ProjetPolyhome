package com.monfort.projetpolyhome.components

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.utils.CommandManager

class GroupCommandPanel(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs) {

    private val title: TextView
    private val grid: GridLayout

    private val floorNames = mapOf(1 to "RDC", 2 to "Étage")

    init {
        inflate(context, R.layout.group_command, this)
        title = findViewById(R.id.groupType)
        grid = findViewById(R.id.groupCommands)
    }

    fun bind(type: String, commandManager: CommandManager, sendCommand: (DeviceData, String) -> Unit) {
        title.text = "Commandes groupées"

        val commands = commandManager.filterDevices(type = type)
            .flatMap { it.availableCommands }
            .distinct()

        val floors = commandManager.getFloors()

        grid.columnCount = 1 + floors.size
        grid.removeAllViews()

        for (command in commands) {
            addButton("$command\nTous") {
                commandManager.sendGroupCommand(command, typeFilter = type, sendCommand = sendCommand)
            }

            for (floor in floors) {
                val name = floorNames[floor] ?: "Étage $floor"
                addButton("$command\n$name") {
                    commandManager.sendGroupCommand(command, typeFilter = type, floorFilter = floor, sendCommand = sendCommand)
                }
            }
        }
    }

    private fun addButton(label: String, onClick: () -> Unit) {
        val btn = Button(context).apply {
            text = label
            isFocusable = false
            isFocusableInTouchMode = false
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(4, 4, 4, 4)
            }
            setOnClickListener { onClick() }
        }
        grid.addView(btn)
    }

}