package fr.isen.amara.androidsmartdevice.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.amara.androidsmartdevice.R

@Composable
fun MainScreen(onScanClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBBDEFB),
                        Color(0xFF64B5F6)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AndroidSmartDevice",
                fontSize = 18.sp, // Taille de l'écriture réduite
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = "BLE Device",
                fontSize = 10.sp, // Taille de l'écriture réduite
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "BLE Device Scanner",
                fontSize = 32.sp, // Taille de l'écriture réduite
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.pic_bluetooth),
                contentDescription = "Bluetooth Icon",
                modifier = Modifier
                    .size(200.dp) // Taille de l'image réduite
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
            )

            Text(
                text = "Discover and connect to nearby Bluetooth Low Energy devices with ease. Tap the button below to start your scanning adventure!",
                fontSize = 16.sp, // Taille de l'écriture réduite
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Taille du bouton réduite
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(35.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF42A5F5)) // Bouton plus clair
            ) {
                Text("Start Scanning", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Unleash the power of connectivity",
                fontSize = 16.sp, // Taille de l'écriture réduite
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
