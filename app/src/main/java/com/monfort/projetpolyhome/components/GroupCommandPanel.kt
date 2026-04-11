package com.monfort.projetpolyhome.components

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.monfort.projetpolyhome.R

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


}