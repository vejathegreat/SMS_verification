package com.velaphi.smsverification.utils

interface SmsListener {
    fun onSuccess(otp: String)
    fun onError()
}