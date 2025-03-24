package fr.isen.amara.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.amara.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Active le mode edge-to-edge pour une meilleure expérience utilisateur
        setContent {
            AndroidSmartDeviceTheme {
                MainScreen(
                    onScanClick = {
                        // Navigation vers ScanActivity lorsqu'on clique sur le bouton
                        startActivity(Intent(this, ScanActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(onScanClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre de l'application
        Text(
            text = "AndroidSmartDevice",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description de l'application
        Text(
            text = "Application de gestion d'appareils BLE connectés",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton pour démarrer le scan BLE (navigue vers ScanActivity)
        FilledTonalButton(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Démarrer le scan")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AndroidSmartDeviceTheme {
        MainScreen(onScanClick = {})
    }
}
