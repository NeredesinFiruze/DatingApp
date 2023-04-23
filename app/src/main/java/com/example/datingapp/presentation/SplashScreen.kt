package com.example.datingapp.presentation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.datingapp.presentation.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(navController: NavController) {

    val homeViewModel = hiltViewModel<HomeViewModel>()
    val signIn by homeViewModel.signInComplete.value.collectAsStateWithLifecycle(false)

    var value by remember { mutableStateOf(12) }
    val size by animateIntAsState(
        targetValue = value,
        animationSpec = spring(Spring.DampingRatioMediumBouncy)
    )

    LaunchedEffect(Unit) {
        value = 32
        delay(1.seconds)
        val destination = if (FirebaseAuth.getInstance().currentUser == null) "sig-in"
                          else if (signIn == true) "home"
                          else "on-boarding"

        navController.navigate(destination)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Splash", fontSize = size.sp)
    }
}