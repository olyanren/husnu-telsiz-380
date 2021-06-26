package com.dengetelekom.telsiz.helpers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface


object AlertHelper {
     fun showInfoDialog(context: Context, title: String, message: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Tamam"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }
}