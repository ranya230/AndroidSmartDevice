package fr.isen.amara.androidsmartdevice.composable

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("MissingPermission")
@Composable
fun ScanScreen(
    devices: List<BluetoothDevice>,
    isScanning: Boolean,
    onToggleScan: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    // Filtrage des appareils en fonction de la recherche
    val filteredDevices = devices.filter {
        // On vérifie si le nom ou l'adresse du périphérique correspond à la recherche
        it.name?.contains(searchQuery, ignoreCase = true) == true ||
                it.address.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF64B5F6))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        TopAppBar(
            title = { Text("AndroidSmartDevice", color = Color.White, textAlign = TextAlign.Center) }
        )

        // Barre de recherche
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search by name or address") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") }
        )

        // Bouton pour démarrer ou arrêter le scan
        ScanButton(isScanning, onToggleScan)

        // Message pendant le scan
        AnimatedVisibility(visible = isScanning) {
            Text("Scanning for devices...", color = Color.White, fontSize = 18.sp)
        }

        // Affichage des appareils filtrés
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(filteredDevices) { device ->
                DeviceItem(device, onDeviceClick)
            }
        }
    }
}

@Composable
fun ScanButton(isScanning: Boolean, onToggleScan: () -> Unit) {
    Button(
        onClick = onToggleScan,
        modifier = Modifier.size(100.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isScanning) Color.Red else Color.Green
        )
    ) {
        Icon(
            imageVector = if (isScanning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = "Toggle Scan",
            tint = Color.White
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, onDeviceClick: (BluetoothDevice) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onDeviceClick(device) },
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = device.name ?: "Unknown Device", style = MaterialTheme.typography.h6)
            Text(text = device.address, style = MaterialTheme.typography.body2, color = Color.Gray)
        }
    }
}
