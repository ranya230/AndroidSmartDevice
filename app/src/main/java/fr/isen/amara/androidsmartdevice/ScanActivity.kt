package fr.isen.amara.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import fr.isen.amara.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat

class ScanActivity : ComponentActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var isScanning by mutableStateOf(false)
    private var scanResults by mutableStateOf(emptyList<BluetoothDevice>())
    private var searchQuery by mutableStateOf("")

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // All permissions granted, start scanning
            toggleScan()
        } else {
            // Handle permission denial
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        setContent {
            AndroidSmartDeviceTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ScanScreen(
                        devices = scanResults,
                        isScanning = isScanning,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { searchQuery = it },
                        onToggleScan = {
                            if (bluetoothAdapter.isEnabled) {
                                if (hasPermissions()) {
                                    toggleScan()
                                } else {
                                    requestPermissions()
                                }
                            } else {
                                // Demander à l'utilisateur d'activer le Bluetooth
                                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                startActivityForResult(enableBtIntent, 1)
                            }
                        },
                        onDeviceClick = { device ->
                            // Naviguer vers ConnectActivity en passant le nom et l'adresse de l'appareil
                            val intent = Intent(this, ConnectActivity::class.java)
                            intent.putExtra("DEVICE_NAME", device.name ?: "Unknown Device")
                            intent.putExtra("DEVICE_ADDRESS", device.address)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    private fun hasPermissions(): Boolean {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        requestMultiplePermissions.launch(requiredPermissions)
    }

    @SuppressLint("MissingPermission")
    private fun toggleScan() {
        if (isScanning) {
            bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
        } else {
            scanResults = emptyList() // Clear previous results before starting new scan
            bluetoothAdapter.bluetoothLeScanner?.startScan(scanCallback)
        }
        isScanning = !isScanning
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (result.device !in scanResults) {
                scanResults = scanResults + result.device // Add device to the list
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Toast.makeText(this@ScanActivity, "Scan failed with error code: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop scanning when the activity is no longer in the foreground
        if (isScanning) {
            toggleScan()
        }
    }
}

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
    val filteredDevices = remember(devices, searchQuery) {
        devices.filter {
            it.name?.contains(searchQuery, ignoreCase = true) == true ||
                    it.address.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFBBDEFB), Color(0xFF64B5F6)))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "AndroidSmartDevice",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            backgroundColor = Color(0xFF1E88E5)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Search Devices") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search Icon")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ScanButton(isScanning = isScanning, onToggleScan = onToggleScan)

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isScanning,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = "Scanning for devices...",
                fontSize = 18.sp,
                color = Color(0xFF1A237E),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Text(
            text = "Devices found: ${filteredDevices.size}",
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(filteredDevices) { device ->
                DeviceItem(device, onDeviceClick)
            }
        }
    }
}

@Composable
fun ScanButton(isScanning: Boolean, onToggleScan: () -> Unit) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            scale.animateTo(1f)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .scale(scale.value)
    ) {
        Button(
            onClick = onToggleScan,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isScanning) Color(0xFF0D47A1) else Color(0xFF1E88E5)
            ),
            modifier = Modifier.size(120.dp)
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "Stop Scan",
                tint = Color.White
            )
        }

        // Pulsing Circle
        if (isScanning) {
            PulsingCircle()
        }
    }
}

@Composable
fun PulsingCircle() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(130.dp)
            .scale(pulseAnim)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
    )
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = device.name ?: "Unknown Device", // Handle devices with no name
                style = MaterialTheme.typography.h6,
                color = Color.Black
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}
