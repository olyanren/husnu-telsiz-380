package com.dengetelekom.telsiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.dengetelekom.telsiz.ui.UrgentNotificationActivity

class MainActivity : AppCompatActivity() {
    private lateinit var textCompanyName: TextView
    private lateinit var btnRefresh: Button
    private lateinit var btnPreviousRecord: Button
    private lateinit var btnUrgentNotification: ImageView
    private lateinit var btnLogout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnRefresh = findViewById(R.id.btn_refresh)
        btnPreviousRecord = findViewById(R.id.btn_previous)
        btnUrgentNotification = findViewById(R.id.btn_urgent_notification)
        btnLogout = findViewById(R.id.btn_logout)
        val transceiverViewModel = ViewModelProvider(this,
                TransceiverViewModelFactory(repository = TransceiverRepository())).get(
                TransceiverViewModel::class.java)

        transceiverViewModel.refreshState.observe(this,
                Observer { loginFormState ->
                    when (loginFormState) {
                        null -> return@Observer
                        "VISIBLE" -> {
                            btnRefresh.visibility = View.VISIBLE; btnPreviousRecord.visibility = View.VISIBLE;btnLogout.visibility=View.VISIBLE
                            btnUrgentNotification.visibility=View.VISIBLE
                        }
                        "GONE" -> {
                            btnRefresh.visibility = View.GONE; btnPreviousRecord.visibility = View.GONE;btnLogout.visibility=View.GONE
                            btnUrgentNotification.visibility=View.GONE
                        }
                    }
                })
        transceiverViewModel.companyNameState.observe(this,
                { companyNameState -> textCompanyName.text = companyNameState })
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            finish()
        }
        btnUrgentNotification.setOnClickListener {
            startActivity(
                Intent(this, UrgentNotificationActivity::class.java),
            )
        }
        btnRefresh.setOnClickListener {
            transceiverViewModel.refresh()
        }
        btnPreviousRecord.setOnClickListener {
            transceiverViewModel.previousRecord()
        }
        textCompanyName = findViewById(R.id.text_company_name)

    }

}