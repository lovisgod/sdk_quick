package com.interswitchng.smartpos.modules.main.transfer

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.interswitchng.smartpos.R
import com.tapadoo.alerter.Alerter

fun EditText.getTextValue(): String {
    return this.text.toString()
}

fun View.reveal() {
     this.visibility = View.VISIBLE
}
fun View.makeActive() {
    this.alpha = 1.0f
    this.isClickable = true
    this.isActivated = true
}

fun View.makeInActive() {
    this.alpha = 0.4f
    this.isClickable = false
    this.isActivated = false
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Dialog.showDialog(message: String) {
//    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//    this.show()
}

fun Dialog.stop() {
    this.dismiss()
}

fun TextInputEditText.clear() {
    this.setText("")
}


fun customdailog(context: Context?, message: String? = ",", action: (() -> Unit?)? = null): Dialog {
    val dialog = Dialog(context!!, R.style.Theme_AppCompat)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val customProgressBinding = LayoutInflater.from(context).inflate(R.layout.isw_custom_dialog, null, false)
    dialog.setContentView(customProgressBinding)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    val layoutParams = dialog.window!!.attributes
    layoutParams.dimAmount = 0.7f
    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.window!!.setGravity(Gravity.CENTER)
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    if (action != null) {
        customProgressBinding.findViewById<ProgressBar>(R.id.progress_circular).hide()
        customProgressBinding.findViewById<MaterialTextView>(R.id.isw_dialog_message).reveal()
        customProgressBinding.findViewById<MaterialTextView>(R.id.isw_dialog_message).text = message
        customProgressBinding.findViewById<MaterialButton>(R.id.isw_dialog_proceed_btn).reveal()
        customProgressBinding.findViewById<MaterialButton>(R.id.isw_dialog_proceed_btn).setOnClickListener {
            action.invoke()
        }
    }
    dialog.show()
    return dialog
}

fun showSnack(view: View, message: String) {
    Snackbar.make(view,message, Snackbar.LENGTH_LONG).show()
}

fun showErrorAlert(message: String, activity: Activity) {
    Alerter.create(activity)
            .setTitle("Error")
            .setText(message)
            .setDuration(1000)
            .enableVibration(true)
            .enableSwipeToDismiss()
            .setBackgroundColorRes(R.color.iswTextColorError)
            .show()
}

fun showSuccessAlert(message: String, activity: Activity) {
    Alerter.create(activity)
            .setTitle("Error")
            .setText(message)
            .setDuration(1000)
            .enableVibration(true)
            .enableSwipeToDismiss()
            .setBackgroundColorRes(R.color.iswColorPrimary)
            .show()
}