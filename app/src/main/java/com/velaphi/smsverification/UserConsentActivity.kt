package com.velaphi.smsverification

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.android.synthetic.main.activity_user_consent.*
import java.util.*
import java.util.regex.Pattern

class UserConsentActivity : AppCompatActivity() {

    private var sender: Boolean = false
    private val TAG = UserConsentActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_consent)
        toggleNumber()
        setToolbar()
        start_button.setOnClickListener {
            startOtpValidation()
        }

    }

    private fun setToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.sms_user_consent_api)
    }


    private fun toggleNumber() {
        number_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.with_number -> {
                    phone_number_input_layout.visibility = View.VISIBLE
                    sender = true
                }
                else -> {
                    phone_number_input_layout.visibility = View.GONE
                    sender = false
                    phone_number_edit_text.text?.clear()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun startOtpValidation() {
        otpSmsTask()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        Objects.requireNonNull(this)
            .registerReceiver(smsVerificationReceiver, intentFilter)
    }


    private fun otpSmsTask() {
        val number: String?

        if (!phone_number_edit_text.text.isNullOrEmpty() && sender) {
            number = phone_number_edit_text.text.toString()
        } else {
            number = null
        }

        val task = SmsRetriever.getClient(this).startSmsUserConsent(number)

        task.addOnCompleteListener { listener ->
            if (listener.isSuccessful) {
                Log.d(TAG, "otpSmsTask: OTP Listener Successful")
            } else {
                val exception = listener.exception
                Log.e(TAG, "otpSmsTask: OTP Listener failed", exception)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SMS_CONSENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                val smsMessage = data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                if (smsMessage != null) {
                    val oneTimePin = parseOneTimePinSms(smsMessage)
                    if (!oneTimePin.isNullOrEmpty()) {
                        otp_edit_text_input.setText(oneTimePin)
                    }
                }
            }
        }
    }

    private fun parseOneTimePinSms(message: String): String? {
        val pattern = Pattern.compile(OTP_SMS_REGEX)
        val matcher = pattern.matcher(message)
        var oneTimePin: String? = null

        if (matcher.find()) {
            oneTimePin = matcher.group()
        }

        return oneTimePin
    }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

                if (smsRetrieverStatus.statusCode == CommonStatusCodes.SUCCESS) {
                    val consentIntent =
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    try {
                        startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                    } catch (exception: ActivityNotFoundException) {
                        Log.e(TAG, "BroadcastReceiver TIMEOUT", exception)
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(smsVerificationReceiver)
    }

    companion object {
        const val SMS_CONSENT_REQUEST = 2
        const val OTP_SMS_REGEX = "\\d{8}"
    }
}
