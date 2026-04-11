package com.monfort.projetpolyhome.utils

import com.monfort.projetpolyhome.data.DeviceData

class CommandManager(
    devices: List<DeviceData> = emptyList()
) {

    var devices: List<DeviceData> = devices
        private set
    fun update(newDevices: List<DeviceData>) {
        devices = newDevices
    }

    fun getFloor(id: String): Int? {
        val first = id.substringBefore(".")
        val number = first.substringAfterLast(" ")
        return number.toIntOrNull()
        // Shutter 1.1 -> [Shutter 1] [1] -> [Shutter] [1] -> RDC
        // Light 2.1 -> [Light 2] [1] -> [Light] [2] -> Étage
    }

    fun getFloors(): List<Int> {
        return devices.mapNotNull { getFloor(it.id) }
            .distinct()
            .sorted()
    }

    fun getTypes(): List<String> {
        return devices.map { it.type }
            .distinct()
    }

    fun filterDevices(type: String? = null, floor: Int? = null): List<DeviceData> {
        return devices.filter { device ->
            val mType = type == null || device.type == type
            val mFloor = floor == null || getFloor(device.id) == floor
            mType && mFloor
        }
    }

    fun sendGroupCommand(
        command: String,
        typeFilter: String? = null,
        floorFilter: Int? = null,
        sendCommand: (DeviceData, String) -> Unit
    ) {
        filterDevices(typeFilter, floorFilter)
            .filter { command in it.availableCommands } // recupère que ceux dont la commande est demandée
            .forEach { sendCommand(it, command) } // applique à tous ceux d'avant la fonction passée en param
    }

}