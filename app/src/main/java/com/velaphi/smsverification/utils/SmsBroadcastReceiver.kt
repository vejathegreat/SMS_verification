package com.velaphi.smsverification.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {

    private val otpPattern = "(\\d{8})".toRegex()

    override fun onReceive(context: Context?, intent: Intent?) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    val otp: MatchResult? = otpPattern.find(sms)
                    if (!otp?.value.isNullOrEmpty()) {
                        otp?.value?.let { smsListener.onSuccess(it) }
                    } else {
                        smsListener.onError()
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    smsListener.onError()
                }
            }
        }
    }

    companion object {
        private lateinit var smsListener: SmsListener

        fun bindListener(smsListener: SmsListener) {
            this.smsListener = smsListener
        }
    }
}