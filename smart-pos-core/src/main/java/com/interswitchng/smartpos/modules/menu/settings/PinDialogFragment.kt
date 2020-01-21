package com.interswitchng.smartpos.modules.menu.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
//import android.support.design.widget.BottomSheetBehavior
//import android.support.design.widget.BottomSheetDialog
//import android.support.design.widget.BottomSheetDialogFragment
//import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.utilities.SecurityUtils.getSecurePassword
import kotlinx.android.synthetic.main.isw_content_pin_dialog.*
import com.interswitchng.smartpos.shared.utilities.toast
import org.koin.android.ext.android.inject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * This class presents password screen for authorizing
 * admin access to terminal configuration changes
 */
class PinDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private val store: KeyValueStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ISW_FullScreenDialogStyleTransparent)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ///val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        // set click listener to prevent drag on the dialog
//        dialog.setOnShowListener {
//            val sheetDialog = it as BottomSheetDialog
           // val bottomSheet: FrameLayout? = sheetDialog.findViewById( com.google.android.material.R.id.design_bottom_sheet)

//            val behavior = BottomSheetBehavior.from(bottomSheet)
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED


//            val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
//            val behavior=  BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
//
//
//            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
//            })
//        }


        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val sheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog

        return dialog
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.isw_content_pin_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add click listeners for pin buttons
        one.setOnClickListener(this)
        two.setOnClickListener(this)
        three.setOnClickListener(this)
        four.setOnClickListener(this)
        five.setOnClickListener(this)
        six.setOnClickListener(this)
        seven.setOnClickListener(this)
        eight.setOnClickListener(this)
        nine.setOnClickListener(this)
        oneZero.setOnClickListener(this)

        // add click and long click for delete
        val delete = view.findViewById<ImageView>(R.id.delete)
        delete.setOnClickListener(this)

        // clear text on long click
        delete.setOnLongClickListener { _ ->
            userPin.text = null
            return@setOnLongClickListener true
        }

        // dismiss dialog if closed
//        closeBtn.setOnClickListener { dialog.dismiss(); }
    }

    override fun onClick(view: View) {
        var result = userPin.text.toString()
        if (result.length == 6) return

        when (view.id) {
            R.id.one -> result += "1"
            R.id.two -> result += "2"
            R.id.three -> result += "3"
            R.id.four -> result += "4"
            R.id.five -> result += "5"
            R.id.six -> result += "6"
            R.id.seven -> result += "7"
            R.id.eight -> result += "8"
            R.id.nine -> result += "9"
            R.id.oneZero -> result += "0"
            R.id.delete -> if (result.isNotEmpty()) {
                result = result.substring(0, result.length - 1)
            }
            else -> return
        }


        // set the text
        userPin.setText(result)

        // if pin is complete authorize user
        if (result.length == 6) {
            if (isValidPin(result)) {
                // dismiss dialog
                dismiss()

                // navigate to terminal settings
                val activity = requireActivity()
                val intent = Intent(activity, TerminalSettingsActivity::class.java)
                activity.startActivity(intent)
            }

            // reset user's pin
            userPin.text = null
        }
    }

    private fun isValidPin(pin: String): Boolean {
        // get admin configured pin
        var existingPin = store.getString(Constants.KEY_ADMIN_PIN, "")
        if (existingPin.isEmpty()) existingPin = BuildConfig.ISW_DEFAULT_PIN

        // hash provided pin
        var providedPin = getSecurePassword(pin)
        val isValid = providedPin == existingPin

        if (isValid) requireContext().toast("PIN OK")
        else requireContext().toast("Invalid PIN")

        return isValid
    }

}