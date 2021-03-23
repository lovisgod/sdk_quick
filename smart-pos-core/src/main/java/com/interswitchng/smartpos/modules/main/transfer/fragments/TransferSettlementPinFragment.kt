package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.PinEditText
import com.interswitchng.smartpos.modules.main.transfer.*
import com.interswitchng.smartpos.modules.menu.settings.TerminalSettingsActivity
import com.interswitchng.smartpos.shared.Constants
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_transfer_settlement_pin.*
import kotlinx.android.synthetic.main.isw_layout_insert_password.*
import kotlinx.android.synthetic.main.isw_layout_insert_pin_reuseable.*
import kotlinx.android.synthetic.main.isw_layout_insert_pin_reuseable.isw_pin_text
import java.util.*


class TransferSettlementPinFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(this.requireContext()).inflate(R.layout.isw_fragment_transfer_settlement_pin, container, false)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//         val dialog  =super.onCreateDialog(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ISWCustomBottomSheetDialog)
//        return dialog
//    }

    override fun getTheme(): Int {
        return R.style.ISWCustomBottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        isw_button_pin_proceed_reuseable.makeInActive()
        isw_button_pass_proceed_reuseable.makeInActive()
        isw_pin_edit_text_reusable.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkPinState(isw_pin_edit_text_reusable)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               // do nothing
            }
        })

        isw_pass_edit_text_reusable.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length >= 7)
                {
                    isw_pass_edit_text_reusable.hideKeyboard()
                    isw_button_pass_proceed_reuseable.makeActive()
                } else {
                    isw_button_pass_proceed_reuseable.makeInActive()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }
        })

        checkForPin()
    }

    private fun checkForPin() {
        val pinAvailable = Prefs.getString(Constants.SETTLEMENT_PIN_SET, "")
        if (pinAvailable.isNullOrEmpty()) {
            if (this.arguments != null && this.requireArguments().getString("configure_pass", "").isNotEmpty()) {
                isw_insert_pin_settlement_layout.hide()
                isw_insert_password_configure_layout.reveal()
            }else {
                isw_pin_text.text = "Enter a new settlement pin"
            }

        } else  if (this.arguments != null && this.requireArguments().getString("configure_pass", "").isNotEmpty()) {
            isw_insert_pin_settlement_layout.hide()
            isw_insert_password_configure_layout.reveal()
        } else {
            if (this.arguments != null && this.requireArguments().getBoolean("reset_pin", false)) {
                isw_pin_text.text = "Enter your current settlement pin"
            } else {
                isw_pin_text.text = "Enter your settlement pin"
            }
        }
    }

    private fun setupClickListener() {
        isw_button_pin_proceed_reuseable.setOnClickListener {
            performSetup()
        }

        isw_button_pass_proceed_reuseable.setOnClickListener {
            if (isw_pass_edit_text_reusable.text.toString() == Constants.DEFAULT_CONFIGURE_PASSWORD) {
                showSuccessAlert("Details correct", this.requireActivity())
                this.dismiss()
                showScreen(TerminalSettingsActivity::class.java)
            } else {
                showErrorAlert("Authentication failed please try again with correct details or contact support!!!", this.requireActivity())
            }
        }
    }

    private fun checkPinState(pinLayout: PinEditText){
        if (pinLayout.text.toString().length < 6) {
            isw_button_pin_proceed_reuseable.makeInActive()
        } else {
            pinLayout.hideKeyboard()
            isw_button_pin_proceed_reuseable.makeActive()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    this@TransferSettlementPinFragment.requireActivity().runOnUiThread {
                        performSetup()
                    }
                }

            }, 200)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferSettlementPinFragment()
        val TAG = "TRANSFER SETTLEMENT PIN FRAGMENT"
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().popBackStack(R.id.isw_transferlandingfragment, false)
    }

    private fun performSetup() {
        if (!isw_pin_edit_text_reusable.text.toString().isNullOrEmpty()) {
            var pin = Prefs.getString(Constants.SETTLEMENT_PIN_SET, "")
            if (!pin.isNullOrEmpty()) {
                if(pin !=isw_pin_edit_text_reusable.text.toString()) {
                    showErrorAlert("Pin Does not match!!!!", this.requireActivity())
                    isw_pin_edit_text_reusable.clear()
                } else {
                    if (this.arguments != null && this.requireArguments().getBoolean("reset_pin", false)) {
                        Prefs.remove(Constants.SETTLEMENT_PIN_SET)
                        showSuccessAlert("Pin Reset Successful", this.requireActivity())
                        this.dismiss()
                    } else {
                        showSuccessAlert("Pin OK!!!!", this.requireActivity())
                        // navigate to the input page
                        var bundle = Bundle()
                        bundle.putBoolean(Constants.FOR_SETTLEMENT_ACCOUNT_SETUP, true)
                        findNavController().navigate(R.id.isw_transferinputfragment, bundle)
                    }

                }
            } else {
                showSuccessAlert("Pin OK!!!!", this.requireActivity())
                // navigate to the input page
                var bundle = Bundle()
                bundle.putBoolean(Constants.FOR_SETTLEMENT_ACCOUNT_SETUP, true)
                Prefs.putString(Constants.SETTLEMENT_PIN_SET, isw_pin_edit_text_reusable.text.toString())
                findNavController().navigate(R.id.isw_transferinputfragment, bundle)
            }

        }
    }

    private fun showScreen(clazz: Class<*>) {
        val intent = Intent(this.requireContext(), clazz).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (clazz.isAssignableFrom(TerminalSettingsActivity::class.java)) {
            intent.putExtra("FROM_SETTINGS", true)
        }
        this.requireContext().startActivity(intent)
    }
}