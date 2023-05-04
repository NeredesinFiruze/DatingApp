package com.example.datingapp.navigation

sealed class Screen(val route: String){
    object Splash: Screen("splash")
    object SignIn: Screen("sign-in")
    object SignInPhone: Screen("sign-in-phone")
    object OnBoarding: Screen("on-boarding")
    object Home: Screen("home")
    object Chat: Screen("chat")
    object ChatList: Screen("chat-list")
    object Filter: Screen("filter")
    object Settings: Screen("settings")
}
