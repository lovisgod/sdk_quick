package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.PinEditText
import com.interswitchng.smartpos.modules.main.transfer.makeActive
import com.interswitchng.smartpos.modules.main.transfer.makeInActive
import com.interswitchng.smartpos.modules.main.transfer.showErrorAlert
import com.interswitchng.smartpos.modules.main.transfer.showSuccessAlert
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_transfer_settlement_pin.*
import kotlinx.android.synthetic.main.isw_layout_insert_pin_reuseable.*

class TransferSettlementPinFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(this.requireContext()).inflate(R.layout.isw_fragment_transfer_settlement_pin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
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
    }

    private fun setupClickListener() {
        isw_button_pin_proceed_reuseable.setOnClickListener {
            if (!isw_pin_edit_text_reusable.text.toString().isNullOrEmpty()) {
                var pin = Constants.SETTLEMENT_PIN
                if(pin !=isw_pin_edit_text_reusable.text.toString()) {
                    showErrorAlert("Pin Does not match!!!!", this.requireActivity())
                } else {
                    showSuccessAlert("Pin OK!!!!", this.requireActivity())
                    // navigate to the input page
                    var bundle = Bundle()
                    bundle.putBoolean(Constants.FOR_SETTLEMENT_ACCOUNT_SETUP, true)
                    findNavController().navigate(R.id.isw_transferinputfragment, bundle)
                }
            }
        }
    }

    private fun checkPinState(pinLayout: PinEditText){
        if (pinLayout.text.toString().length < 6) {
            isw_button_pin_proceed_reuseable.makeInActive()
        } else {
            isw_button_pin_proceed_reuseable.makeActive()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferSettlementPinFragment()
        val TAG = "TRANSFER SETTLEMENT PIN FRAGMENT"
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().popBackStack(R.id.isw_transferlandingfragment, true)
    }
}