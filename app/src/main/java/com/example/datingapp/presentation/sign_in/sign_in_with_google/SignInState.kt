package com.example.datingapp.presentation.sign_in.sign_in_with_google

data class SignInState(
    val isSuccessful: Boolean = false,
    val signInErrorMessage: String? = null
)
