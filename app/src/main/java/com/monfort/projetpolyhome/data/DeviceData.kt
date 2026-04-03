package com.monfort.projetpolyhome.data

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: List<String>,

    // pas dispo pour toutes les commandes
    val opening: Float? = null,
    val openingMode: Int? = null,
    val power: Int? = null
)
