package com.dengetelekom.telsiz.helpers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.dengetelekom.telsiz.R


object AlertHelper {
     fun showInfoDialog(context: Context, title: String, message: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getText(R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }
}