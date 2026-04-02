package com.monfort.projetpolyhome.data

data class Device(
    val id: String,
    val type: String,
    val availableCommands: List<String>,

    // pas dispo pour toutes les commandes
    val opening: Int? = null,
    val openingMode: Int? = null,
    val power: Int? = null
)
