package com.example.datingapp.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.datingapp.ui.theme.TextColor

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        SettingItem(text = "See the source code") {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/NeredesinFiruze/DatingApp")
            ContextCompat.startActivity(context, openURL, null)
        }
        SettingItem(
            text = "Sign Out",
            isLast = true,
            onClick = onSignOut
        )
    }
}

@Composable
fun SettingItem(
    text: String,
    isLast: Boolean = false,
    onClick: ()-> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = TextColor,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
    if (!isLast) Divider()
}