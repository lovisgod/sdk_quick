package com.interswitchng.smartpos.modules.main.transfer

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.PinEditText
import com.interswitchng.smartpos.modules.main.billPayment.models.AirtimeMapperClass
import com.interswitchng.smartpos.modules.main.billPayment.models.Response
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_transfer_fragment_amount.*
import java.text.NumberFormat

fun EditText.getTextValue(): String {
    return this.text.toString()
}

fun TextInputEditText.getTextValue(): String {
    return this.text.toString()
}

fun View.reveal() {
     this.visibility = View.VISIBLE
}
fun View.makeActive() {
    this.alpha = 1.0f
    this.isClickable = true
    this.isActivated = true
    this.isEnabled = true
}

fun View.makeInActive() {
    this.alpha = 0.4f
    this.isClickable = false
    this.isActivated = false
    this.isEnabled = false
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.isVisible(visibility: Boolean) {
    this.visibility = if(visibility) {
        View.VISIBLE
    } else {
        View.GONE
    }
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

fun PinEditText.clear() {
    this.setText("")
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
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
        customProgressBinding.findViewById<LottieAnimationView>(R.id.animation_view).hide()
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

fun showErrorDialog(context: Context, message: String? = ",", action: (() -> Unit?)? = null): Dialog {
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

    customProgressBinding.findViewById<ImageView>(R.id.top_image).setImageResource(
            R.drawable.ic_not_successful
    )
    customProgressBinding.findViewById<MaterialTextView>(R.id.isw_dialog_message).text = message
    if (action  != null) {
        customProgressBinding.findViewById<MaterialButton>(R.id.isw_dialog_proceed_btn).reveal()
    } else {
        customProgressBinding.findViewById<MaterialButton>(R.id.isw_dialog_proceed_btn).hide()
    }
    customProgressBinding.findViewById<MaterialButton>(R.id.isw_dialog_proceed_btn).setOnClickListener {
        action?.invoke()
        dialog.dismiss()
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
            .setDuration(2000)
            .enableVibration(true)
            .enableSwipeToDismiss()
            .setBackgroundColorRes(R.color.iswTextColorError)
            .show()
}

fun showSuccessAlert(message: String, activity: Activity) {
    Alerter.create(activity)
            .setTitle("Success")
            .setText(message)
            .setDuration(2000)
            .enableVibration(true)
            .enableSwipeToDismiss()
            .setBackgroundColorRes(R.color.iswColorPrimary)
            .show()
}

fun changeListener(fields: ArrayList<TextInputEditText>, action: (() -> Any?)? = null) {
    for( i in fields) {
        i.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                action?.invoke()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }
}

 fun updateAmount(digit: String): String? {
    val parsed = java.lang.Double.parseDouble(digit)
    val numberFormat = NumberFormat.getInstance()
    numberFormat.minimumFractionDigits = 2
    numberFormat.maximumFractionDigits = 2
    val formatted = numberFormat.format(parsed / 100)
     println(formatted)
    return formatted
}

fun getDataFromRechargeResponse(resp: String): AirtimeMapperClass {
    val refPattern =  """SessionAdd\(\'refnum\',\'[0-9]{1,20}\'\)""".toRegex()
    val codePattern = """SessionAdd\(\'rcode\',\'[0-9]{1,20}\'\)""".toRegex()

    val messagePattern =  """SessionAdd\(\'__myrmsg\',\'[a-z, A-Z]{1,30}\'\)""".toRegex()
    var ref = refPattern.find(resp)?.value!!.split(",").get(1).split(")")?.get(0)
    println(ref)
    var code = codePattern.find(resp)?.value!!.split(",").get(1).split(")")?.get(0)
    println(code)

    var message = messagePattern.find(resp)?.value!!.split(",").get(1).split(")")?.get(0)
    println(message)

    return AirtimeMapperClass( Response(code, message, ref))
}