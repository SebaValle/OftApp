package com.example.oftapp.util

import java.security.MessageDigest

object SecurityUtils {
    private const val SALT = "OftApp_VcM_2026_SecureSalt"

    fun hashPassword(password: String): String {
        val textoConSalt = "${password.trim()}:$SALT"
        val bytes = MessageDigest.getInstance("SHA-256").digest(textoConSalt.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}