package com.example.hmi.feature.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hmi.core.ui.components.EmergencyHUD
import com.example.hmi.core.ui.components.IndustrialButton
import com.example.hmi.core.ui.components.IndustrialInput
import com.example.hmi.core.ui.components.TelemetryCard
import com.example.hmi.core.ui.theme.HealthStatus
import com.example.hmi.core.ui.theme.StitchTheme
import com.example.hmi.feature.diagnostics.model.TelemetryData

@Composable
fun DiagnosticsScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val telemetryItems = remember {
        listOf(
            TelemetryData("1", "ENGINE I", "1200", "RPM", HealthStatus.NORMAL),
            TelemetryData("2", "PRESSURE", "45.2", "PSI", HealthStatus.CAUTION),
            TelemetryData("3", "TEMP", "98.6", "C", HealthStatus.NORMAL),
            TelemetryData("4", "VOLTAGE", "24.1", "V", HealthStatus.NORMAL),
            TelemetryData("5", "FUEL", "12", "%", HealthStatus.CRITICAL),
            TelemetryData("6", "LOAD", "85", "%", HealthStatus.CAUTION)
        )
    }

    // Determine system status
    val systemStatus = remember(telemetryItems) {
        if (telemetryItems.any { it.status == HealthStatus.CRITICAL }) {
            HealthStatus.CRITICAL
        } else if (telemetryItems.any { it.status == HealthStatus.CAUTION }) {
            HealthStatus.CAUTION
        } else {
            HealthStatus.NORMAL
        }
    }

    StitchTheme {
        EmergencyHUD(status = systemStatus, modifier = modifier) {
            Scaffold(
                topBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SYSTEM DIAGNOSTICS",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Industrial Input for filtering
                    IndustrialInput(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = "SEARCH TELEMETRY",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Modular Grid Layout
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(telemetryItems.filter { it.label.contains(searchQuery, ignoreCase = true) }) { item ->
                            TelemetryCard(
                                label = item.label,
                                value = item.value,
                                unit = item.unit,
                                status = item.status
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IndustrialButton(
                            onClick = { /* TODO: Refresh */ },
                            label = "REFRESH SYSTEM",
                            modifier = Modifier.weight(1f)
                        )
                        IndustrialButton(
                            onClick = { /* TODO: Emergency Stop */ },
                            label = "EMERGENCY STOP",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
