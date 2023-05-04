package com.example.datingapp

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.datingapp.composables.BottomNavigationBar
import com.example.datingapp.data.local.ConnectionInfo
import com.example.datingapp.data.local.listOfBottomNavItem
import com.example.datingapp.navigation.NavSetup
import com.example.datingapp.presentation.sign_in_screen.sign_in_with_google.GoogleAuthUiClient
import com.example.datingapp.ui.theme.DateFlirtAppTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            applicationContext,
            Identity.getSignInClient(applicationContext)
        )
    }

    val telephonyManager by lazy { getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private fun observeConnectionStatus() {

        if (userId.isNullOrEmpty())return
        val database = FirebaseDatabase.getInstance()
        val path = database.getReference("users")
            .child(userId)
            .child("connectionStatus")

        path.setValue(ConnectionInfo(true, null))

        path.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                path.onDisconnect().setValue(
                    ConnectionInfo(
                        connectionStatus = false,
                        lastConnected = System.currentTimeMillis().toString()
                    )
                )
            }
            override fun onCancelled(error: DatabaseError) = Unit
        })
    }
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeConnectionStatus()
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
                            destination == "on-boarding"
                        ) return@Scaffold
                        else {
                            BottomNavigationBar(
                                items = listOfBottomNavItem,
                                navController = navController,
                                onItemClick = { navController.navigate(it.route) }
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        NavSetup(
                            navController = navController,
                            context = this@MainActivity,
                            googleAuthUiClient = googleAuthUiClient,
                            telephonyManager = telephonyManager
                        )
                    }
                }
            }
        }
    }
}