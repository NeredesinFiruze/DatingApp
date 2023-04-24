package com.example.datingapp.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datingapp.composables.BackButton

@Composable
fun ChatScreen(navController: NavController) {
    Column(Modifier.fillMaxSize()) {
        ChatTopInfo(navController)
    }
}

@Composable
fun ChatTopInfo(navController: NavController) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackButton { navController.popBackStack() }
        Text(text = "user")
    }
}