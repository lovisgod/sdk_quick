package com.interswitchng.smartpos.modules.main.transfer

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.interswitchng.smartpos.R

fun EditText.getTextValue(): String {
    return this.text.toString()
}

fun View.reveal() {
     this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Dialog.showDialog(message: String) {
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    this.show()
}

fun Dialog.stop() {
    this.dismiss()
}

fun TextInputEditText.clear() {
    this.setText("")
}


fun customdailog(context: Context?): Dialog {
    val dialog = Dialog(context!!, R.style.Theme_AppCompat)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val customProgressBinding = LayoutInflater.from(context).inflate(R.layout.isw_custom_dialog, null, false)
    dialog.setContentView(customProgressBinding)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    val layoutParams = dialog.window!!.attributes
    layoutParams.dimAmount = 0.7f
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.window!!.setGravity(Gravity.CENTER)
    dialog.window!!.attributes = layoutParams
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return dialog
}