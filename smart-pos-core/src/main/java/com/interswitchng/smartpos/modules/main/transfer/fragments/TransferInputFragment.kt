package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.TransferViewModel
import com.interswitchng.smartpos.modules.main.transfer.customdailog
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.CallbackListener
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.modules.main.transfer.showSuccessAlert
import com.interswitchng.smartpos.modules.main.transfer.utils.BankFilterDialog
import com.interswitchng.smartpos.modules.main.transfer.utils.LoadingDialog
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.toast
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_bills.*
import kotlinx.android.synthetic.main.isw_fragment_qr_code.*
import kotlinx.android.synthetic.main.isw_fragment_transfer_input.*
import kotlinx.android.synthetic.main.isw_fragment_transfer_input.backImg
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class TransferInputFragment : BaseFragment(TAG), CallbackListener {

    private var bankList = arrayListOf<BankModel>()
    lateinit var _selectedBank: BankModel
    lateinit var submitButton: Button
    lateinit var accountNumberEditor: EditText
    lateinit var _beneficiaryPayload: BeneficiaryModel
    var isValid = false
    lateinit var dialog: Dialog


    var accountNumber: String? = ""

    private val transferViewModel: TransferViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bankList.addAll(Constants.BANK_LIST.sortedWith(compareBy { it.bankName }))
        submitButton = isw_transfer_input_proceed
        accountNumberEditor = isw_transfer_input_account



        validateInput()
        observeViewModel()

        isw_transfer_input_bank.setOnClickListener {
            fragmentManager?.let { it1 -> BankFilterDialog(this).show(it1, "show-bank-filter") }
        }

        accountNumberEditor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        this@TransferInputFragment.requireActivity().runOnUiThread {
                            validateBeneficiary()
                        }
                    }

                }, 300)
                accountNumber = s.toString()
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
        bankText.setText(data.bankName)
        validateBeneficiary()
    }


    private fun validateBeneficiary() {
        if (accountNumber?.length == 10 && this::_selectedBank.isInitialized) {
            if (!this::dialog.isInitialized) {
                dialog = customdailog(this.requireContext())
            }else {
                dialog.show()
            }
            transferViewModel.validateBankDetails(_selectedBank.selBankCodes!!, accountNumber!!)
        } else {
            isValid = false
            toggleAccountNameVisibility()
            validateInput()
        }
    }

    private fun validateInput() {
        submitButton.alpha = if (!isValid) 0.5F else 1F
        submitButton.isEnabled = isValid
        submitButton.isClickable = isValid
    }

    private fun submitForm(view: View) {
        if (isValid) {
            if (this.requireArguments().getBoolean(Constants.FOR_SETTLEMENT_ACCOUNT_SETUP, false)) {
                Prefs.putString(Constants.SETTLEMENT_ACCOUNT_NUMBER, accountNumber)
                Prefs.putString(Constants.SETTLEMENT_BANK_CODE, _selectedBank.recvInstId)
                Prefs.putString(Constants.SETTLEMENT_BANK_NAME, _selectedBank.bankName)
                Prefs.putString(Constants.SETTLEMENT_ACCOUNT_NAME, _beneficiaryPayload.accountName)
                showSuccessAlert("Settlement account setup completed", this.requireActivity())
                findNavController().popBackStack(R.id.isw_transferlandingfragment, false)
            } else {
                val payment = PaymentModel()
                payment.type = PaymentModel.TransactionType.TRANSFER
                val action = TransferInputFragmentDirections.iswActionIswTransferinputfragmentToIswAmountfragment(paymentModel = payment, BankModel = _selectedBank, BeneficiaryModel = _beneficiaryPayload)
                view.findNavController().navigate(action)
            }
        }

    }

    private fun observeViewModel() {
        backImg.setOnClickListener {
            navigateUp()
        }

        val owner = { lifecycle }

        with(transferViewModel) {
            //observe the benefiary call
            beneficiary.observe(owner) {
                it?.let { beneficiary ->
                    dialog.let { d->
                        d.dismiss()
                    }
                    when (beneficiary) {
                        is Some -> {
                            println("this beneficary ${beneficiary.value}")
                            _beneficiaryPayload = beneficiary.value
                            isValid = true

                        }
                        is None -> {
                            isValid = false
                            context?.toast("Name enquiry error")
                        }
                    }
                    toggleAccountNameVisibility()
                    validateInput()
                }
            }
        }
    }

    private fun toggleAccountNameVisibility() {
        if (isValid) {
            account_name.visibility = View.VISIBLE
            isw_transfer_input_account_name.visibility = View.VISIBLE
            isw_transfer_input_account_name.setText(_beneficiaryPayload.accountName)
        } else {
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