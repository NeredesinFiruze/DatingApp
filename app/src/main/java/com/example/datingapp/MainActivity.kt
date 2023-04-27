package com.example.datingapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.datingapp.composables.BottomNavigationBar
import com.example.datingapp.data.local.listOfBottomNavItem
import com.example.datingapp.presentation.chat.ChatListScreen
import com.example.datingapp.presentation.FilterScreen
import com.example.datingapp.presentation.home.HomeScreen
import com.example.datingapp.presentation.SettingsScreen
import com.example.datingapp.presentation.SplashScreen
import com.example.datingapp.presentation.chat.ChatScreen
import com.example.datingapp.presentation.chat.ChatViewModel
import com.example.datingapp.presentation.on_boarding.OnBoarding
import com.example.datingapp.presentation.sign_in_screen.GoogleAuthUiClient
import com.example.datingapp.presentation.sign_in_screen.SignInScreen
import com.example.datingapp.presentation.sign_in_screen.SignInViewModel
import com.example.datingapp.ui.theme.DateFlirtAppTheme
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            applicationContext,
            Identity.getSignInClient(applicationContext)
        )
    }
//    private val auth by lazy {
//        FirebaseAuth.getInstance()
//    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DateFlirtAppTheme {
                val navController = rememberNavController()
                val backStackEntry = navController.currentBackStackEntryAsState()
                val destination = backStackEntry.value?.destination?.route
                Scaffold(
                    bottomBar = {
                        if (destination == null ||
                            destination == "sign-in" ||
                            destination == "splash" ||
                            destination == "chat" ||
                            destination == "on-boarding") return@Scaffold
                        else{
                            BottomNavigationBar(
                                items = listOfBottomNavItem,
                                navController = navController,
                                onItemClick = { navController.navigate(it.route) }
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        Navigation(navController = navController, this@MainActivity)
                    }
                }
            }
        }
    }
    @Composable
    fun Navigation(navController: NavHostController,context: Context) {

        val chatViewModel = hiltViewModel<ChatViewModel>()
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            composable("splash"){
                SplashScreen(navController)
            }
            composable("sign-in"){
                val viewModel = hiltViewModel<SignInViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if(result.resultCode == RESULT_OK) {
                            lifecycleScope.launch {
                                val signInResult = googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )
                LaunchedEffect(key1 = state.isSuccessful){
                    if (state.isSuccessful){
                        Toast.makeText(applicationContext,"sign in successful", Toast.LENGTH_LONG).show()
                        navController.navigate("on-boarding")
                        viewModel.resetState()
                    }
                }
                SignInScreen(
                    state = state,
                    onSignInClick = {
                        lifecycleScope.launch {
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
            composable("on-boarding"){
                OnBoarding(navController, context)
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("chat-list") {
                ChatListScreen(navController,chatViewModel = chatViewModel)
            }
            composable("chat") {
                ChatScreen(navController,chatViewModel)
            }
            composable("settings") {
                SettingsScreen(
                    onSignOut = {
                        lifecycleScope.launch {
                            googleAuthUiClient.signOut()
                        }
                        navController.navigate("sign-in")
                    }
                )
            }
            composable("filter") {
                FilterScreen()
            }
        }
    }
}