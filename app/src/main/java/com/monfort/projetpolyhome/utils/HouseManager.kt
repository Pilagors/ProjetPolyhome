package com.monfort.projetpolyhome.utils

import android.content.Context

class HouseManager(context: Context) {
    private val prefId = context.getSharedPreferences("houseId", Context.MODE_PRIVATE)
    private val prefOwner = context.getSharedPreferences("owner", Context.MODE_PRIVATE)

    companion object {
        private const val HOUSE_ID_KEY = "houseId"
        private const val PREVIOUS_HOUSE_ID_KEY = "prevHouseId"
        private const val HOUSE_OWNER_KEY = "owner"

    }

    fun saveHouseId(houseId: Int) {
        prefId.edit().putInt(HOUSE_ID_KEY, houseId).apply()
    }

    fun getHouseId(): Int {
        return prefId.getInt(HOUSE_ID_KEY, -1)
    }

    fun saveOwner(owner : Boolean){
        prefOwner.edit().putBoolean(HOUSE_OWNER_KEY,owner).apply()
    }

    fun isOwner() : Boolean{
        return prefOwner.getBoolean(HOUSE_OWNER_KEY,false)
    }

    fun hasHouseIdChanged(): Boolean {
        val current = getHouseId()
        val previous = prefId.getInt(PREVIOUS_HOUSE_ID_KEY, -2) // éviter egal -1 comme house_id_key

        if (current != previous) {
            prefId.edit().putInt(PREVIOUS_HOUSE_ID_KEY, current).apply()
            return true

        } else {
            return false
        }
    }

    fun logout() {
        prefId.edit().remove(HOUSE_ID_KEY).remove(PREVIOUS_HOUSE_ID_KEY).apply()
        prefOwner.edit().remove(HOUSE_OWNER_KEY).apply()
    }
}
