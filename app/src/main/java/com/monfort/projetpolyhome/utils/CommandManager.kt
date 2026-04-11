package com.monfort.projetpolyhome.utils

import com.monfort.projetpolyhome.data.DeviceData

class CommandManager(
    devices: List<DeviceData> = emptyList()
) {

    private var devices: List<DeviceData> = devices

    fun update(newDevices: List<DeviceData>) {
        devices = newDevices
    }

    fun getFloor(id: String): Int? {
        return id.split(".").firstOrNull()?.toIntOrNull()
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