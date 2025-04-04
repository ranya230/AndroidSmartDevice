package fr.isen.amara.androidsmartdevice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.isen.amara.androidsmartdevice.composable.MainScreen
import fr.isen.amara.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDeviceTheme {
                MainScreen(onScanClick = {
                    startActivity(Intent(this, ScanActivity::class.java))
                })
            }
        }
    }
}