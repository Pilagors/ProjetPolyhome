package com.monfort.projetpolyhome.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.google.android.material.button.MaterialButton
import com.monfort.projetpolyhome.R
import com.monfort.projetpolyhome.data.DeviceData
import com.monfort.projetpolyhome.utils.CommandManager

class GroupCommandPanel(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs) {

    private val title: TextView
    private val grid: GridLayout
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val floorNames = mapOf(1 to "RDC", 2 to "Étage")
    
    private val colorGlobal = "#00838F"
    private val colorRDC = "#00ACC1"
    private val colorEtage = "#4DD0E1"

    init {
        inflate(context, R.layout.group_command, this)
        title = findViewById(R.id.groupType)
        grid = findViewById(R.id.groupCommands)
    }

    fun bind(type: String, commandManager: CommandManager, sendCommand: (DeviceData, String) -> Unit) {
        title.text = "Commandes groupées"
        title.setTextColor(Color.parseColor("#00363E"))

        val commands = commandManager.filterDevices(type = type)
            .flatMap { it.availableCommands }
            .distinct()

        val floors = commandManager.getFloors()

        grid.columnCount = 1 + floors.size
        grid.removeAllViews()

        for (command in commands) {
            addButton(command, "Tous", colorGlobal) {
                commandManager.sendGroupCommand(command, typeFilter = type, sendCommand = sendCommand)
            }

            for (floor in floors) {
                val name = floorNames[floor] ?: "Étage $floor"
                val color = if (floor == 1) colorRDC else colorEtage
                
                addButton(command, name, color) {
                    commandManager.sendGroupCommand(command, typeFilter = type, floorFilter = floor, sendCommand = sendCommand)
                }
            }
        }
    }

    private fun addButton(command: String, subtitle: String, colorHex: String, onClick: () -> Unit) {
        val btn = inflater.inflate(R.layout.group_command_button, grid, false) as MaterialButton
        
        // Version beaucoup plus simple avec du HTML
        val htmlText = "$command<br/><small><i>$subtitle</i></small>"
        
        btn.apply {
            text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            isFocusable = false
            isFocusableInTouchMode = false
            backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorHex))
            setOnClickListener { onClick() }
        }
        grid.addView(btn)
    }
}