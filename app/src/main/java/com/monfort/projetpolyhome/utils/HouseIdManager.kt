package com.monfort.projetpolyhome.utils

import android.content.Context

class HouseIdManager(context: Context) {
    private val prefs = context.getSharedPreferences("houseId", Context.MODE_PRIVATE)

    companion object {
        private const val HOUSE_ID_KEY = "houseId"
    }

    fun saveHouseId(houseId: Int) {
        prefs.edit().putInt(HOUSE_ID_KEY, houseId).apply()
    }

    fun getHouseId(): Int {
        return prefs.getInt(HOUSE_ID_KEY, -1)
    }

    fun logout() {
        prefs.edit().remove(HOUSE_ID_KEY).apply()
    }
}
