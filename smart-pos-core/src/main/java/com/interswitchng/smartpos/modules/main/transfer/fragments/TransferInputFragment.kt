package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.transfer.adapters.BankAutoCompleteAdapter
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_transfer_input.*

class TransferInputFragment : BaseFragment(TAG) {
    private var bankList = arrayListOf<BankModel>()
    lateinit var _selectedBank: BankModel
    lateinit var submitButton: Button

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bankList.addAll(Constants.BANK_LIST.sortedWith(compareBy { it.bankName }))
    }

//TODO: Implement viemodel / databinding

    fun setUpAutoComplete() {
        val auto = isw_bank_autocomplete_text_view
        val adapter = context?.let {
            BankAutoCompleteAdapter(
                    it,
                R.layout.isw_bank_autocomplete_row, bankList
        )
        }
        auto.threshold = 0
        auto.setAdapter(adapter)
        submitButton = isw_transfer_amount_proceed

        auto.setOnItemClickListener { parent, view, position, id ->
            var selectedBank = parent.getItemAtPosition(position) as BankModel
            closeKeyBoard()
            Log.d("ItemClicked", selectedBank.selBankCodes.toString())
        }

        auto.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                auto.showDropDown()
            }
        }

//        TODO: Clear selected bank once the user does a delete activity


    }

    fun validateInput() {

        submitButton.isEnabled = false
        submitButton.isClickable = false
    }


    private fun closeKeyBoard() {
        getActivity()?.getWindow()?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferInputFragment()
        val TAG = "TRANSFER INPUT FRAGMENT"
    }
}