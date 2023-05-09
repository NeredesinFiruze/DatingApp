package com.example.datingapp.navigation

import android.content.Context
import android.location.LocationManager
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.datingapp.presentation.FilterScreen
import com.example.datingapp.presentation.SettingsScreen
import com.example.datingapp.presentation.SplashScreen
import com.example.datingapp.presentation.chat.ChatListScreen
import com.example.datingapp.presentation.chat.ChatScreen
import com.example.datingapp.presentation.chat.ChatViewModel
import com.example.datingapp.presentation.home.HomeScreen
import com.example.datingapp.presentation.home.HomeViewModel
import com.example.datingapp.presentation.on_boarding.OnBoarding
import com.example.datingapp.presentation.sign_in.SignInScreen
import com.example.datingapp.presentation.sign_in.SignInViewModel
import com.example.datingapp.presentation.sign_in.sign_in_with_google.GoogleAuthUiClient
import com.example.datingapp.presentation.sign_in.sign_in_with_phone.SignInWithPhone
import kotlinx.coroutines.launch

@Composable
fun NavSetup(
    navController: NavHostController,
    context: Context,
    googleAuthUiClient: GoogleAuthUiClient,
    telephonyManager: TelephonyManager,
    locationManager: LocationManager,
) {
    val chatViewModel = hiltViewModel<ChatViewModel>()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.SignIn.route) {
            val viewModel = hiltViewModel<SignInViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == ComponentActivity.RESULT_OK) {
                        scope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                        }
                    }
                }
            )
            LaunchedEffect(state) {
                if (state.isSuccessful) {
                    val destination = viewModel.destination()
                    navController.navigate(destination)
                    viewModel.resetState()
                }
            }
            SignInScreen(
                state = state,
                navController = navController,
                onSignInClick = {
                    scope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }
        composable(Screen.SignInPhone.route) {
            SignInWithPhone(navController, telephonyManager, context)
        }
        composable(Screen.OnBoarding.route) {
            OnBoarding(navController, context, locationManager, googleAuthUiClient)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, context)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController, chatViewModel)
        }
        composable(Screen.ChatList.route) {
            ChatListScreen(navController, chatViewModel = chatViewModel)
        }
        composable(Screen.Filter.route) {
            FilterScreen(navController)
        }
        composable(Screen.Settings.route) {
            val homeViewModel = hiltViewModel<HomeViewModel>()

            SettingsScreen(
                onSignOut = {
                    homeViewModel.completeSignIn(false)
                    scope.launch {
                        googleAuthUiClient.signOut()
                    }
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }
    }
}