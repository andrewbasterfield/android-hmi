package com.example.hmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hmi.connection.ConnectionScreen
import com.example.hmi.dashboard.DashboardScreen
import com.example.hmi.protocol.ConnectionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Obtain the ViewModel at the activity level to monitor global connection state
                    val connectionViewModel: com.example.hmi.connection.ConnectionViewModel = hiltViewModel()
                    val connectionState by connectionViewModel.connectionState.collectAsState()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()

                    // Automatically return to connection screen if connection drops
                    LaunchedEffect(connectionState) {
                        val isDashboard = currentBackStackEntry?.destination?.route == "dashboard"
                        if (isDashboard && (connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.ERROR)) {
                            navController.navigate("connection") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = "connection") {
                        composable("connection") {
                            ConnectionScreen(
                                viewModel = connectionViewModel,
                                onConnected = {
                                    navController.navigate("dashboard") {
                                        popUpTo("connection") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateBack = {
                                    connectionViewModel.disconnect()
                                    // Navigation handled by the LaunchedEffect above upon disconnect
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}