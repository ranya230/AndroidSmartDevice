import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*

@Composable
fun LedControlScreen(bluetoothGatt: BluetoothGatt?, services: List<BluetoothGattService>) {
    // Initialisation des états des LEDs
    var led1State by remember { mutableStateOf(false) }
    var led2State by remember { mutableStateOf(false) }
    var led3State by remember { mutableStateOf(false) }

    // Fonction pour envoyer la commande BLE
    @SuppressLint("MissingPermission")
    fun sendLedCommand(ledNumber: Int, state: Boolean) {
        val ledCommand = when (ledNumber) {
            1 -> if (state) 0x01 else 0x00
            2 -> if (state) 0x02 else 0x00
            3 -> if (state) 0x03 else 0x00
            else -> 0x00
        }

        val characteristic = services[2].characteristics[0]
        characteristic.value = byteArrayOf(ledCommand.toByte())

        // Assurez-vous que vous avez un BluetoothGatt valide et que la caractéristique est valide
        bluetoothGatt?.writeCharacteristic(characteristic)
    }

    // Layout pour afficher les LEDs
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titre de l'écran
        Text(
            text = "LED Control",
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Affichage des LEDs avec des boutons cliquables
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // LED 1
            Button(
                onClick = {
                    led1State = !led1State
                    sendLedCommand(1, led1State)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (led1State) Color.Green else Color.Gray
                ),
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            ) {
                Text("LED 1", color = Color.White)
            }

            // LED 2
            Button(
                onClick = {
                    led2State = !led2State
                    sendLedCommand(2, led2State)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (led2State) Color.Green else Color.Gray
                ),
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            ) {
                Text("LED 2", color = Color.White)
            }

            // LED 3
            Button(
                onClick = {
                    led3State = !led3State
                    sendLedCommand(3, led3State)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (led3State) Color.Green else Color.Gray
                ),
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            ) {
                Text("LED 3", color = Color.White)
            }
        }
    }
}
