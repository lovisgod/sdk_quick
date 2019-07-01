package com.interswitchng.smartpos.shared.utilities

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import com.interswitchng.smartpos.R
import java.util.*

internal object DialogUtils {

    fun getLoadingDialog(context: Context): AlertDialog {
        return AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.isw_title_generating_code)
                .setMessage(R.string.isw_title_loading)
                .create()
    }

    fun getAlertDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
                .setTitle("An Error Occurred")
                .setMessage("Unable to generate code")
    }

    fun getEmailInputDialog(context: Context, eventHandler: (String?) -> Unit): AlertDialog {

        val view = LayoutInflater.from(context).inflate(R.layout.isw_send_email_dialog, null)
        val emailEditText = view.findViewById<EditText>(R.id.emailInputEditText)

        val builder = AlertDialog.Builder(context)
                .setTitle("Enter Email")
                .setView(view)

        builder.setPositiveButton("SUBMIT") { dialog, _ ->
            val email = emailEditText.text.trim().toString()
            eventHandler(email)
            dialog.dismiss()
        }

        builder.setNegativeButton("CANCEL") { dialog, _ ->
            eventHandler(null)
            dialog.dismiss()
        }

        return builder.create()
    }


    fun createDateDialog(context: Context, listener: DatePickerDialog.OnDateSetListener): DatePickerDialog {

        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(context, R.style.IswDatePicker, listener, year, month, day)
    }

}