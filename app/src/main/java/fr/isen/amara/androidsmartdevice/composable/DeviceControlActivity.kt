package fr.isen.amara.androidsmartdevice.composable

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.amara.androidsmartdevice.R
import fr.isen.amara.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
import java.util.*

class DeviceControlActivity : ComponentActivity() {

    private var bluetoothGatt: BluetoothGatt? = null
    private var services: List<BluetoothGattService> = emptyList()
    private var isConnected = false
    private var buttonClickCount by mutableStateOf(0)
    private var thirdButtonClickCount by mutableStateOf(0)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceName = intent.getStringExtra("DEVICE_NAME") ?: "Unknown Device"
        val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS") ?: "No Address"

        connectToDevice(deviceAddress)

        setContent {
            AndroidSmartDeviceTheme {
                var connectionState by remember { mutableStateOf("Connecting...") }
                var isConnecting by remember { mutableStateOf(true) }

                LaunchedEffect(isConnected) {
                    if (isConnected) {
                        connectionState = "Connected to the device"
                        isConnecting = false
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            connectionState = "Connected to the device"
                            isConnecting = false
                        }, 3000)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E88E5).copy(alpha = 0.3f), Color(0xFF42A5F5).copy(alpha = 0.5f))
                            )
                        ),
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

                    if (isConnecting) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = connectionState,
                                fontSize = 24.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            CircularProgressIndicator(color = Color(0xFF1E88E5))

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Device Name: $deviceName",
                                fontSize = 18.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = "Device Address: $deviceAddress",
                                fontSize = 18.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    } else {
                        TPBLEScreen()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(deviceAddress: String) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)

        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BLE", "Connected to GATT server.")
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BLE", "Disconnected from GATT server.")
                    isConnected = false
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("BLE", "Services discovered")
                    services = gatt.services
                    isConnected = true
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                try {
                    if (services.isNotEmpty()) {
                        when {
                            services[2].characteristics[1].uuid == characteristic.uuid -> {
                                val value = characteristic.value[0].toInt()
                                buttonClickCount = value
                            }
                            services[3].characteristics[0].uuid == characteristic.uuid -> {
                                val value = characteristic.value[0].toInt()
                                thirdButtonClickCount = value
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BLE", "Error processing characteristic change: ${e.message}")
                }
            }
        }

        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    private fun toggleLed(ledNumber: Int, isOn: Boolean) {
        if (bluetoothGatt != null && services.isNotEmpty()) {
            try {
                val ledCharacteristic = services[2].characteristics[0]
                val value = when (ledNumber) {
                    1 -> if (isOn) 0x01 else 0x00
                    2 -> if (isOn) 0x02 else 0x00
                    3 -> if (isOn) 0x03 else 0x00
                    else -> 0x00
                }

                ledCharacteristic.value = byteArrayOf(value.toByte())
                bluetoothGatt?.writeCharacteristic(ledCharacteristic)
            } catch (e: Exception) {
                Log.e("BLE", "Error writing LED characteristic: ${e.message}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToButtonNotifications(subscribe: Boolean) {
        if (bluetoothGatt != null && services.isNotEmpty()) {
            try {
                val button1Characteristic = services[2].characteristics[1]
                bluetoothGatt?.setCharacteristicNotification(button1Characteristic, subscribe)

                val descriptor = button1Characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor.value = if (subscribe) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(descriptor)

                val button3Characteristic = services[3].characteristics[0]
                bluetoothGatt?.setCharacteristicNotification(button3Characteristic, subscribe)

                val descriptor3 = button3Characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor3.value = if (subscribe) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(descriptor3)
            } catch (e: Exception) {
                Log.e("BLE", "Error subscribing to notifications: ${e.message}")
            }
        }
    }

    @Composable
    fun TPBLEScreen() {
        var led1State by remember { mutableStateOf(false) }
        var led2State by remember { mutableStateOf(false) }
        var led3State by remember { mutableStateOf(false) }
        var isSubscribedToNotifications by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TPBLE",
                fontSize = 24.sp,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "LED Display",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LedIcon(isActive = led1State, onClick = {
                    led1State = !led1State
                    toggleLed(1, led1State)
                })

                LedIcon(isActive = led2State, onClick = {
                    led2State = !led2State
                    toggleLed(2, led2State)
                })

                LedIcon(isActive = led3State, onClick = {
                    led3State = !led3State
                    toggleLed(3, led3State)
                })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Subscribe to receive the increment count",
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Text(
                    text = "RECEIVE",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Checkbox(
                    checked = isSubscribedToNotifications,
                    onCheckedChange = { checked ->
                        isSubscribedToNotifications = checked
                        subscribeToButtonNotifications(checked)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF1976D2),
                        uncheckedColor = Color.Gray
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Count: $buttonClickCount",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }

    @Composable
    fun LedIcon(isActive: Boolean, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = if (isActive) R.drawable.led_on else R.drawable.led_off),
                contentDescription = "LED",
                modifier = Modifier.size(60.dp)
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
    }
}