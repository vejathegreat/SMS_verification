package com.velaphi.smsverification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AutomaticVerificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_automatic_verification)
        setToolbar()
    }

    private fun setToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.automatic_verification_api)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
