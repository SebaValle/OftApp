package com.example.oftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.oftapp.ui.screens.OftAppNavGraph
import com.example.oftapp.ui.theme.OftAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OftAppTheme {
                val navController = rememberNavController()
                OftAppNavGraph(navController = navController)
            }
        }
    }
}