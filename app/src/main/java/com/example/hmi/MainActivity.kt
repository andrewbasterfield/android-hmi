package com.example.hmi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hmi.connection.ConnectionScreen
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.dashboard.DashboardScreen
import com.example.hmi.dashboard.ImportSelectionDialog
import com.example.hmi.data.ConfigTransferManager
import com.example.hmi.data.TransferEvent
import com.example.hmi.protocol.ConnectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var transferManager: ConfigTransferManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            StitchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }
                    
                    // Obtain the ViewModel at the activity level to monitor global connection state
                    val connectionViewModel: com.example.hmi.connection.ConnectionViewModel = hiltViewModel()
                    val connectionState by connectionViewModel.connectionState.collectAsState()
                    val keepScreenOnSetting by connectionViewModel.keepScreenOn.collectAsState()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()

                    val isDashboard = currentBackStackEntry?.destination?.route == "dashboard"

                    var pendingBackup by remember { mutableStateOf<com.example.hmi.data.FullBackupPackage?>(null) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        transferManager.events.collect { event ->
                            when (event) {
                                is TransferEvent.ImportReady -> {
                                    pendingBackup = event.backup
                                }
                                is TransferEvent.Success -> {
                                    snackbarHostState.showSnackbar(event.message)
                                }
                                is TransferEvent.Error -> {
                                    snackbarHostState.showSnackbar(event.message)
                                }
                                is TransferEvent.ValidationError -> {
                                    snackbarHostState.showSnackbar("Validation Error: ${event.message}")
                                }
                            }
                        }
                    }

                    if (pendingBackup != null) {
                        ImportSelectionDialog(
                            backup = pendingBackup!!,
                            onDismiss = { pendingBackup = null },
                            onConfirm = { importLayout, importProfiles ->
                                scope.launch {
                                    transferManager.executeImport(pendingBackup!!, importLayout, importProfiles)
                                    pendingBackup = null
                                    
                                    // FR-013: Redirect to dashboard on successful import
                                    if (importLayout) {
                                        navController.navigate("dashboard") {
                                            popUpTo("connection") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    // Handle Keep Screen On window flag
                    DisposableEffect(isDashboard, keepScreenOnSetting) {
                        if (isDashboard && keepScreenOnSetting) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                        onDispose {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                    }

                    // Automatically return to connection screen if connection drops
                    LaunchedEffect(connectionState) {
                        if (isDashboard && (connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.ERROR)) {
                            // Industrial Hysteresis: Wait 5s before kicking user out to allow for auto-reconnect
                            kotlinx.coroutines.delay(5000)
                            
                            // Check again after delay
                            if (isDashboard && (connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.ERROR)) {
                                navController.navigate("connection") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        }
                    }

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { padding ->
                        NavHost(
                            navController = navController, 
                            startDestination = "connection",
                            modifier = Modifier.padding(padding)
                        ) {
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
                                        navController.navigate("connection") {
                                            popUpTo("dashboard") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_VIEW == action && type == "application/json") {
            intent.data?.let { uri ->
                lifecycleScope.launch {
                    transferManager.importFullBackup(uri)
                }
            }
        } else if (Intent.ACTION_SEND == action && type == "application/json") {
            @Suppress("DEPRECATION")
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            uri?.let {
                lifecycleScope.launch {
                    transferManager.importFullBackup(it)
                }
            }
        }
    }
}
