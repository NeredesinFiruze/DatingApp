package com.example.datingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
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
import com.example.datingapp.navigation.Screen
import com.example.datingapp.presentation.sign_in_screen.sign_in_with_google.GoogleAuthUiClient
import com.example.datingapp.presentation.sign_in_screen.sign_in_with_phone.smsCode
import com.example.datingapp.ui.theme.DateFlirtAppTheme
import com.example.datingapp.util.SmsBroadcastReceiver
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
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
    private val telephonyManager by lazy { getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }
    private val locationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val smsReceiver = SmsBroadcastReceiver()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeConnectionStatus()

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsReceiver,intentFilter,SmsRetriever.SEND_PERMISSION,null)
        SmsRetriever.getClient(this).startSmsUserConsent(null)

        setContent {
            DateFlirtAppTheme {
                val navController = rememberNavController()
                val backStackEntry = navController.currentBackStackEntryAsState()
                val destination = backStackEntry.value?.destination?.route
                Scaffold(
                    bottomBar = {
                        if (destination == null ||
                            destination == Screen.Splash.route ||
                            destination == Screen.SignIn.route ||
                            destination == Screen.SignInPhone.route ||
                            destination == Screen.OnBoarding.route ||
                            destination == Screen.Chat.route
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
                            telephonyManager = telephonyManager,
                            locationManager = locationManager
                        )
                    }
                }
            }
        }
    }
    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsReceiver)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQ_USER_CONSENT ->
                if (resultCode == Activity.RESULT_OK && data != null){
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val regex = Regex("\\d{6}")
                    val filterOTP = message?.let { regex.find(it) }?.value
                    smsCode.value = filterOTP ?: ""
                }
        }
    }
    private fun observeConnectionStatus() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

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
    companion object{
        const val REQ_USER_CONSENT = 1444
    }
}