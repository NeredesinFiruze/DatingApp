package com.example.datingapp.presentation.sign_in

import androidx.lifecycle.ViewModel
import com.example.datingapp.navigation.Screen
import com.example.datingapp.presentation.sign_in.sign_in_with_google.SignInState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel@Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update {
            it.copy(
                isSuccessful = result.data != null,
                signInErrorMessage = result.errorMessage
            )
        }
    }

    suspend fun destination(): String = coroutineScope {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        val path = Firebase.database.getReference("users")
            .child(userId)
            .child("connectionStatus")

        val deferred = CompletableDeferred<String>()

        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                val destination = if (exists) Screen.Home.route else Screen.OnBoarding.route
                deferred.complete(destination)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        })

        deferred.await()
    }

    fun resetState() = _state.update { SignInState() }
}