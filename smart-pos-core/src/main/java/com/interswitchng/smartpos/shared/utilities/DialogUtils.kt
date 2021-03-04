package com.interswitchng.smartpos.shared.utilities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
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

//    fun getNetworkDialog(context: Context, handler: () -> Unit): AlertDialog {
//        return AlertDialog.Builder(context)
//                .setTitle("No Internet Connection")
//                .setMessage("This device is not connected to internet, please put on the mobile data or wifi connection, and try again.")
//                .setPositiveButton("Try Again") { dialog, _ ->
//                    // execute handler
//                    handler()
//                    // dismiss dialog
//                    dialog.dismiss()
//                }
//                .create()
//    }

    fun getNetworkDialog(context: Context, handler: () -> Unit): Dialog {
        val dialog =Dialog(context!!, R.style.ISWCustomAlertDialog)
        val customProgressBinding = LayoutInflater.from(context).inflate(R.layout.isw_custom_message_dialog, null, false)
        dialog.setContentView(customProgressBinding)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        val layoutParams = dialog.window!!.attributes
        layoutParams.dimAmount = 0.7f
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        customProgressBinding.findViewById<MaterialTextView>(R.id.isw_dialog_message).text =
               context.resources.getString(R.string.isw_concat,
                       "This device is not connected to internet, please put on the mobile data or wifi connection, and try again.")
        customProgressBinding.findViewById<MaterialButton>(R.id.isw_dialog_proceed_btn).setOnClickListener {
            handler.invoke()
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }


    fun getEmailInputDialog(context: Context, eventHandler: (String?) -> Unit): AlertDialog {

        var emailDialog: AlertDialog? = null

        val view = LayoutInflater.from(context).inflate(R.layout.isw_send_email_dialog, null)
        val emailEditText = view.findViewById<EditText>(R.id.emailInputEditText)

        // get and return email dialog
        emailDialog = AlertDialog.Builder(context)
                .setTitle("Enter Email")
                .setView(view)
                .setPositiveButton("SUBMIT") { dialog, _ ->
                    val email = emailEditText.text.trim().toString()
                    // validate email
                    val isValidEmail = !TextUtils.isEmpty(email)
                            && Patterns.EMAIL_ADDRESS.matcher(email).matches()

                    if (isValidEmail) {
                        eventHandler(email)
                    } else {
                        // show alert dialog
                        getAlertDialog(context)
                                .setTitle("Invalid Email")
                                .setMessage("Please input a valid email address")
                                .setPositiveButton(android.R.string.ok) { _, _ -> emailDialog?.show() }
                                .show()

                        context.toast("Invalid email, please input a valid email address.")
                    }
                }
                .setNegativeButton("CANCEL") { dialog, _ ->
                    eventHandler(null)
                    dialog.dismiss()
                }
                .create()


        return emailDialog
    }


    fun createDateDialog(context: Context, listener: DatePickerDialog.OnDateSetListener, date: Date = Date()): DatePickerDialog {

        // Use the current date as the default date in the picker
        return Calendar.getInstance().let {
            it.time = date
            val year = it.get(Calendar.YEAR)
            val month = it.get(Calendar.MONTH)
            val day = it.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog(context, R.style.IswDatePicker, listener, year, month, day)
        }
    }

}