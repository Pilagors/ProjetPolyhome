package com.monfort.projetpolyhome.utils

import android.annotation.SuppressLint
import android.content.Context

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "token"
    }

    @SuppressLint("UseKtx")
    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun isLogged(): Boolean {
        val token = getToken()
        return !token.isNullOrEmpty()
    }

    fun logout() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

}