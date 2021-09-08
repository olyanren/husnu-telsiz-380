package com.dengetelekom.telsiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.models.NfcObject
import com.dengetelekom.telsiz.models.Resource
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.dengetelekom.telsiz.ui.TaskFragmentDirections
import com.dengetelekom.telsiz.ui.UrgentNotificationActivity

class MainActivity : AppCompatActivity() {
    private lateinit var textCompanyName: TextView
    private lateinit var btnGroupMain: LinearLayout
    private lateinit var btnRefresh: Button
    private lateinit var btnCheckIn: Button
    private lateinit var btnNotifiation: Button
    private lateinit var btnCheckout: Button
    private lateinit var btnPreviousRecord: Button
    private lateinit var btnUrgentNotification: Button
    private lateinit var btnLogout: Button
    private var LAUNCH_NFC_ACTIVITY_FOR_CHECKIN = 2
    private var LAUNCH_NFC_ACTIVITY_FOR_CHECKOUT = 3
    private lateinit var transceiverViewModel: TransceiverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGroupMain = findViewById(R.id.btn_group_main)
        btnCheckIn = findViewById(R.id.btn_check_in)
        btnNotifiation = findViewById(R.id.btn_notification)
        btnCheckout = findViewById(R.id.btn_checkout)
        btnRefresh = findViewById(R.id.btn_refresh)
        btnPreviousRecord = findViewById(R.id.btn_previous)
        btnUrgentNotification = findViewById(R.id.btn_urgent_notification)
        btnLogout = findViewById(R.id.btn_logout)
        textCompanyName = findViewById(R.id.text_company_name)

        transceiverViewModel = ViewModelProvider(
                this,
                TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
                TransceiverViewModel::class.java
        )

        transceiverViewModel.refreshState.observe(this,
                Observer { loginFormState ->
                    textCompanyName.text = Constants.COMPANY_NAME
                    btnCheckIn.visibility = if (Constants.IS_CHECKIN_AVAILABLE) View.VISIBLE else View.GONE
                    btnCheckout.visibility = if (Constants.IS_CHECKIN_AVAILABLE) View.VISIBLE else View.GONE
                    btnNotifiation.visibility = if (Constants.IS_NOTIFICATON_AVAILABLE) View.VISIBLE else View.GONE
                    when (loginFormState) {
                        null -> return@Observer
                        "VISIBLE" -> {
                            btnGroupMain.visibility = View.VISIBLE;

                        }
                        "GONE" -> {
                            btnGroupMain.visibility = View.GONE;

                        }
                    }
                })


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
        btnCheckIn.setOnClickListener {
            startActivityForResult(
                    Intent(this, ReadNcfActivity::class.java),
                    LAUNCH_NFC_ACTIVITY_FOR_CHECKIN
            )

        }
        btnCheckout.setOnClickListener {
            startActivityForResult(
                    Intent(this, ReadNcfActivity::class.java),
                    LAUNCH_NFC_ACTIVITY_FOR_CHECKOUT
            )
        }

        btnNotifiation.setOnClickListener {
            if (findNavController(R.id.nav_host_fragment).currentDestination?.id != R.id.notificationFragment) {
                val action =  TaskFragmentDirections.actionTaskFragmentToNotificationFragment()
                findNavController(R.id.nav_host_fragment).navigate(action)
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_NFC_ACTIVITY_FOR_CHECKIN && resultCode == RESULT_OK) {
            checkinRequest(data, resultCode)
        } else if (requestCode == LAUNCH_NFC_ACTIVITY_FOR_CHECKOUT && resultCode == RESULT_OK) {
            checkoutRequest(data, resultCode)
        }
    }

    private fun checkinRequest(data: Intent?, resultCode: Int) {
        val result = data?.getStringExtra("result")
        if (result == null) {
            Toast.makeText(this, R.string.barcode_read_cancelled, Toast.LENGTH_LONG)
                    .show()
            return
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.barcode_read_cancelled, Toast.LENGTH_LONG).show()
            return
        }

        Log.i("DENGETELEKOM_NFC", result)
        transceiverViewModel.checkin(NfcObject(result)).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(this, resource.data?.message, Toast.LENGTH_LONG)
                                .show()
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(this, resource.data?.message, Toast.LENGTH_LONG)
                                .show()
                    }
                    Resource.Status.LOADING -> {
                        Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
        })
    }

    private fun checkoutRequest(data: Intent?, resultCode: Int) {
        val result = data?.getStringExtra("result")
        if (result == null) {
            Toast.makeText(this, R.string.barcode_read_cancelled, Toast.LENGTH_LONG)
                    .show()
            return
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.barcode_read_cancelled, Toast.LENGTH_LONG).show()
            return
        }

        Log.i("DENGETELEKOM_NFC", result)
        transceiverViewModel.checkout(NfcObject(result)).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(this, resource.data?.message, Toast.LENGTH_LONG)
                                .show()
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(this, resource.data?.message, Toast.LENGTH_LONG)
                                .show()
                    }
                    Resource.Status.LOADING -> {
                        Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
        })
    }
}