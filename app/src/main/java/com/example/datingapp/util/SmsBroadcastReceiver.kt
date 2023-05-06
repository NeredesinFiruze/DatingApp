package com.example.datingapp.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.datingapp.MainActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

@Suppress("DEPRECATION")
class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {

            val extras = intent.extras
            val smsStatus: Status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (smsStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val consentIntent =
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    try {
                        if (consentIntent != null) {
                            startActivityForResult(
                                context as Activity,
                                consentIntent,
                                MainActivity.REQ_USER_CONSENT,
                                null
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonStatusCodes.TIMEOUT -> {}
            }
        }
    }
}