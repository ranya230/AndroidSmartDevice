package fr.isen.amara.androidsmartdevice.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanScreen(
    devices: List<String>,
    isScanning: Boolean,
    onToggleScan: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBDEFB), Color(0xFF64B5F6))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BLE Device Scanner",
                fontSize = 34.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            ScanButton(isScanning, onToggleScan)

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isScanning,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    "Scanning for BLE devices...",
                    color = Color(0xFF1A237E),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(devices) { device ->
                    DeviceItem(device)
                }
            }
        }
    }
}

@Composable
fun ScanButton(isScanning: Boolean, onToggleScan: () -> Unit) {
    val scale by animateFloatAsState(if (isScanning) 1.2f else 1f)

    Box(contentAlignment = Alignment.Center) {
        Button(
            onClick = onToggleScan,
            modifier = Modifier
                .size(120.dp)
                .scale(scale),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isScanning) Color(0xFF0D47A1) else Color(0xFF1E88E5)
            )
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isScanning) "Stop Scan" else "Start Scan",
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }

        if (isScanning) {
            PulsingCircle()
        }
    }
}

@Composable
fun PulsingCircle() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0x401E88E5))
    )
}

@Composable
fun DeviceItem(device: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { },
        elevation = 4.dp,
        backgroundColor = Color(0xFF64B5F6)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(12.dp).background(Color(0xFF1E88E5), CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = device, color = Color.White, fontSize = 16.sp)
        }
    }
}
