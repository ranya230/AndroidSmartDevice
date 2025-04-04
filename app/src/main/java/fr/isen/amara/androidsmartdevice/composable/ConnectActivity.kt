package fr.isen.amara.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.amara.androidsmartdevice.composable.DeviceControlActivity
import fr.isen.amara.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ConnectActivity : ComponentActivity() {

    private var bluetoothGatt: BluetoothGatt? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceName = intent.getStringExtra("DEVICE_NAME") ?: "Unknown Device"
        val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS") ?: "No Address"

        setContent {
            AndroidSmartDeviceTheme {
                ConnectScreen(deviceName, deviceAddress) {
                    navigateToDeviceControl(deviceName, deviceAddress)
                }
            }
        }
    }

    private fun navigateToDeviceControl(deviceName: String, deviceAddress: String) {
        val intent = Intent(this, DeviceControlActivity::class.java).apply {
            putExtra("DEVICE_NAME", deviceName)
            putExtra("DEVICE_ADDRESS", deviceAddress)
        }
        startActivity(intent)
    }
}

@Composable
fun ConnectScreen(deviceName: String, deviceAddress: String, onConnectClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF64B5F6))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Connecting to Device",
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Name: $deviceName",
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Address: $deviceAddress",
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onConnectClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF42A5F5))
            ) {
                Text(text = "Connect", color = Color.White)
            }
        }
    }
}
