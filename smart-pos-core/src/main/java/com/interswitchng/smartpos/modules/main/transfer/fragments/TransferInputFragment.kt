package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswMainNavGraphDirections
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.TransferViewModel
import com.interswitchng.smartpos.modules.main.transfer.adapters.BankAutoCompleteAdapter
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.utils.LoadingDialog
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_transfer_input.*
import org.koin.android.viewmodel.ext.android.viewModel

class TransferInputFragment : BaseFragment(TAG) {

    // TODO: Move all these to viewModel
    private var bankList = arrayListOf<BankModel>()
    lateinit var _selectedBank: BankModel
    lateinit var submitButton: Button

    private val transferViewModel: TransferViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bankList.addAll(Constants.BANK_LIST.sortedWith(compareBy { it.bankName }))
        observeViewModel()
        transferViewModel.loadBanks()
        submitButton = isw_transfer_input_proceed
        setUpAutoComplete()


        isw_transfer_input_proceed.setOnClickListener { view ->
            submitForm(view)
        }
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

        //        TODO: Clear selected bank once the user does a delete activity


        auto.setOnItemClickListener { parent, view, position, id ->
            var selectedBank = parent.getItemAtPosition(position) as BankModel
            closeKeyBoard()
            context?.toast("ItemClicked " + selectedBank.selBankCodes.toString())
            validateBeneficiary()
        }

        auto.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                auto.showDropDown()
            }
        }

    }

    fun validateBeneficiary() {
        transferViewModel.validateBankDetails()
    }

    fun validateInput() {
//        TODO: write a validator function to validate the input and gray out the submit button
        submitButton.isEnabled = false
        submitButton.isClickable = false
    }

    fun submitForm(view: View) {
        val dialog = LoadingDialog()
        dialog.isCancelable = false
        fragmentManager?.let { dialog.show(it, "show Dialog") }

        val bank = transferViewModel.selectedBank
        val beneficiary = BeneficiaryModel(transferViewModel.accountNumber, transferViewModel.accountNumber)
        val payment = PaymentModel()
        payment.type = PaymentModel.TransactionType.TRANSFER

//        TODO: Push to the next fragment
//        val action = TransferInputFragmentDirections.iswActionIswTransferinputfragmentToIswAmountfragment()
//        view.findNavController().navigate(action)
    }

    private fun observeViewModel() {
        val owner = { lifecycle }

        with(transferViewModel) {
            allBanks.observe(owner) {
                it?.let { banks->
                    when (banks) {
                        is Some -> {
                            context?.toast("Got Bank List")
                        }
                        is None -> {
                            context?.toast("Unable to load bank list")
                        }
                    }
                }
            }

            //observe the benefiary call
            beneficiary.observe(owner) {
                it?.let { banks->
                    when (banks) {
                        is Some -> {
                            context?.toast("Got Beneficiary")
                        }
                        is None -> {
                            context?.toast("Unable to get beneficiary")
                        }
                    }
                }
            }
        }
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