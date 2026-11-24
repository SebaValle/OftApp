package com.example.oftapp

import com.example.oftapp.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun `hashPassword genera hash SHA-256 deterministico de 64 caracteres`() {
        val hash1 = SecurityUtils.hashPassword("123456")
        val hash2 = SecurityUtils.hashPassword("123456")

        assertEquals(64, hash1.length)
        assertEquals(hash1, hash2)
        assertTrue(hash1.all { it in '0'..'9' || it in 'a'..'f' })
    }

    @Test
    fun `hashPassword distingue entre contraseñas diferentes y espacios`() {
        val hashA = SecurityUtils.hashPassword("claveSegura")
        val hashB = SecurityUtils.hashPassword("claveSegura2")

        assertNotEquals("claveSegura", hashA)
        assertNotEquals(hashA, hashB)
    }
}