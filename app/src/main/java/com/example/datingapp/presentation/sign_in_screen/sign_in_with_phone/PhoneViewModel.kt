package com.example.datingapp.presentation.sign_in_screen.sign_in_with_phone

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.datingapp.util.CountryISO
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel @Inject constructor() : ViewModel() {

    val countries = CountryISO.all.toProperties().toList()

    private val auth = FirebaseAuth.getInstance()
    val phoneData = mutableStateOf(PhoneData())

    fun changeIndex(index: Int) {
        phoneData.value = phoneData.value.copy(countryCodeIndex = index)
    }

    fun getOTP(phoneNumber: String, context: Context, onSuccess: ()-> Unit) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationID: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                Toast.makeText(context, verificationID, Toast.LENGTH_LONG).show()
                phoneData.value = phoneData.value.copy(
                    verificationId = verificationID
                )
                onSuccess()
            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, context: Context, onSuccess: () -> Unit) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(context, user.toString(), Toast.LENGTH_LONG).show()
                    Toast.makeText(context, "successful", Toast.LENGTH_LONG).show()
                    onSuccess()
                } else {
                    Toast.makeText(context, "fail", Toast.LENGTH_LONG).show()
                }
            }
    }
}