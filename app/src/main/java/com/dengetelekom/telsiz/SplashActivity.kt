package com.dengetelekom.telsiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import java.util.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMainActivity()
    }


    private fun startMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

}