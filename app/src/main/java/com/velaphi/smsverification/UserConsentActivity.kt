package com.velaphi.smsverification

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.velaphi.smsverification.utils.SmsBroadcastReceiver
import com.velaphi.smsverification.utils.SmsListener
import kotlinx.android.synthetic.main.activity_user_consent.*

class UserConsentActivity : AppCompatActivity() {

    private var sender: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_consent)
        toggleNumber()
        setToolbar()
        start_button.setOnClickListener {
            if (sender) {
                if (!phone_number_edit_text.text.isNullOrEmpty())
                    withSenderNumber(phone_number_edit_text.text.toString())
            } else {
                withoutSenderNumber()
            }
        }

    }

    private fun setToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.sms_user_consent_api)
    }

    private fun withSenderNumber(number: String) {
        val client = SmsRetriever.getClient(this)

        val task = client.startSmsUserConsent(number)

        task.addOnSuccessListener {
            Log.d("CodeActivity", "Sms listener started!")
            listenSms()
        }

        task.addOnFailureListener { e ->
            Log.e("CodeActivity", "Failed to start sms retriever: ${e.message}")
        }
    }

    private fun withoutSenderNumber() {
        val client = SmsRetriever.getClient(this)

        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            Log.d("CodeActivity", "Sms listener started!")
            listenSms()
        }

        task.addOnFailureListener { e ->
            Log.e("CodeActivity", "Failed to start sms retriever: ${e.message}")
        }
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
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun listenSms() {
        SmsBroadcastReceiver.bindListener(object : SmsListener {
            override fun onSuccess(code: String) {
                otp_edit_text_input.setText(code)
            }

            override fun onError() {
                // TODO: Display error message
            }
        })
    }
}
