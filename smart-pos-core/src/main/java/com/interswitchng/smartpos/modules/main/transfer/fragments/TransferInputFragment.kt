package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.TransferViewModel
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.CallbackListener
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.modules.main.transfer.utils.BankFilterDialog
import com.interswitchng.smartpos.modules.main.transfer.utils.LoadingDialog
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_transfer_input.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class TransferInputFragment : BaseFragment(TAG), CallbackListener  {

    private var bankList = arrayListOf<BankModel>()
    lateinit var _selectedBank: BankModel
    lateinit var submitButton: Button
    lateinit var _beneficiaryPayload: BeneficiaryModel
    var isValid = false


    var accountNumber: String? = ""
    var accountName: String? = ""

    private val transferViewModel: TransferViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bankList.addAll(Constants.BANK_LIST.sortedWith(compareBy { it.bankName }))
        observeViewModel()
        submitButton = isw_transfer_input_proceed
        submitButton.isEnabled = false
        submitButton.isClickable = false
        submitButton.alpha = if(!isValid) 0.5F else 1F

        val accountNumberEditor: EditText =  isw_transfer_input_account


        isw_transfer_input_bank.setOnClickListener {
            fragmentManager?.let { it1 -> BankFilterDialog(this).show(it1, "show-bank-filter") }
        }

        accountNumberEditor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Timer().schedule(object : TimerTask () {
                    override fun run() {
                        this@TransferInputFragment.requireActivity().runOnUiThread {
                            validateBeneficiary()
                        }
                    }

                }, 500)
                accountNumber = s.toString()
//                validateBeneficiary()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        isw_transfer_input_proceed.setOnClickListener { view ->
            submitForm(view)
        }
    }

    override fun onDataReceived(data: BankModel) {
        _selectedBank = data
        val bankText: EditText = isw_transfer_input_bank
        bankText.setText(data.bankName.toString())
        validateBeneficiary()
    }



    fun validateBeneficiary() {
        isValid = false
        toggleAccountNameVisibility(false)
        if(accountNumber?.length == 10 && this::_selectedBank.isInitialized) {
            transferViewModel.validateBankDetails(_selectedBank.selBankCodes!!, accountNumber!!)
        }
        else {
                Log.d("Picker", accountNumber)
        }
    }

    fun validateInput() {
        submitButton.isEnabled = isValid
        submitButton.isClickable = isValid
    }

    fun submitForm(view: View) {
        if (isValid) {
            val payment = PaymentModel()
            payment.type = PaymentModel.TransactionType.TRANSFER
            val action = TransferInputFragmentDirections.iswActionIswTransferinputfragmentToIswAmountfragment(paymentModel = payment, BankModel = _selectedBank, BeneficiaryModel = _beneficiaryPayload)
            view.findNavController().navigate(action)
        }

    }


    private fun observeViewModel() {
        val owner = { lifecycle }

        with(transferViewModel) {
            //observe the benefiary call
            beneficiary.observe(owner) {
                it?.let { beneficiary->
                    when (beneficiary) {
                        is Some -> {

                            _beneficiaryPayload = beneficiary.value
                            isValid = true
                            toggleAccountNameVisibility(true)
                        }
                        is None -> {
                            isValid = false
                            toggleAccountNameVisibility(false)
                            context?.toast("Name enquiry error")
                        }
                    }
                    validateInput()
                }
            }
        }
    }

    private fun toggleAccountNameVisibility(state: Boolean) {
      if(state) {
          account_name.visibility = View.VISIBLE
          isw_transfer_input_account_name.visibility = View.VISIBLE
          isw_transfer_input_account_name.setText(_beneficiaryPayload.accountName)
      }  else {
          account_name.visibility = View.GONE
          isw_transfer_input_account_name.visibility = View.GONE
      }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferInputFragment()
        val TAG = "TRANSFER INPUT FRAGMENT"
    }
}