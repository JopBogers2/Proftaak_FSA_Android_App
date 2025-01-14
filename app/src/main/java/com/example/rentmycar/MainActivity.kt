package com.example.rentmycar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.rentmycar.components.BottomNavBar
import com.example.rentmycar.navigation.AppNavigation
import com.example.rentmycar.ui.theme.rentmycarTheme
import com.example.rentmycar.utils.helpers.LocationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the location helper
        locationHelper = LocationHelper(this, this)

        // Initialize the request permissions launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            locationHelper.onRequestPermissionLaunched(allGranted)
        }

        // Check and request the fine & coarse location permissions
        locationHelper.checkAndRequestPermissions(requestPermissionLauncher)

        enableEdgeToEdge()
        setContent {
            rentmycarTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(navController = navController, context)
                    }
                }
            }
        }
    }
}
