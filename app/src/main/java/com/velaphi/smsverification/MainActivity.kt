package com.velaphi.smsverification

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticate_button.setOnClickListener{
            when(radio_group.checkedRadioButtonId){
                R.id.user_consent_radio_button ->  startActivity(Intent(this,UserConsentActivity::class.java))
                R.id.automatic_sms_verification_radio_button -> startActivity(Intent(this,AutomaticVerificationActivity::class.java))
            }
        }
    }
}
