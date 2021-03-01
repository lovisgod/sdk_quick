package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_amount.*
import kotlinx.android.synthetic.main.isw_transfer_fragment_amount.*
import java.text.NumberFormat

class AmountFragment : BaseFragment(TAG) {

    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val payment by lazy { amountFragmentArgs.paymentModel }
    private val bankDetails by lazy { amountFragmentArgs.BankModel }
    private val benefeciaryDetails by lazy { amountFragmentArgs.BeneficiaryModel }

    private val DEFAULT_AMOUNT = "0.00"

    private var amount = Constants.EMPTY_STRING

    override val layoutId: Int
        get() = R.layout.isw_transfer_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        initializeAmount()
        handleProceedToolbarClicks()
        handleDigitsClicks()
    }

    private fun handleProceedToolbarClicks() {
        isw_proceed_transfer.setOnClickListener {
            if (amount == Constants.EMPTY_STRING || amount == DEFAULT_AMOUNT) {
                toast("Enter a valid amount")
            } else {
                proceedWithPayment()
            }
        }
    }

    private fun proceedWithPayment() {
        val latestAmount = isw_amount_transfer.text.toString()
        Logger.with("Amount Fragment").logErr(latestAmount)
        val latestAmountWithoutComma = latestAmount.replace("[$,.]".toRegex(), "")
        Logger.with("Amount Fragment").logErr(latestAmountWithoutComma)
//        val dotIndex = latestAmountWithoutComma.indexOfFirst {
//            it == '.'
//        }

        //val stringWithoutCommaAndDot =  latestAmountWithoutComma.substring(0, dotIndex)
        payment.newPayment {
            amount = latestAmountWithoutComma.toInt()//latestAmount.toDouble()
            formattedAmount = latestAmount

        }

        //cached temporarily the destination account number and receiving institution id
        Prefs.putString("destinationAccountNumber", benefeciaryDetails.accountNumber)
        Prefs.putString("receivingInstitutionId", bankDetails.recvInstId)
        val direction = AmountFragmentDirections.iswActionIswAmountfragmentToIswTransfercardtransactionfragment(payment)
        navigate(direction)
    }

    private fun initializeAmount() {
        amount = DEFAULT_AMOUNT
        isw_amount_transfer.text = amount
    }

    private fun setUpUI() {
        DisplayUtils.hideKeyboard(this.requireActivity())
    }

    private fun handleDigitsClicks() {
        isw_keypad_zero_transfer.setOnClickListener {
            handleClickWithAmountLimit("0")
        }

        isw_keypad_one_transfer.setOnClickListener {
            handleClickWithAmountLimit("1")
        }

        isw_keypad_two_transfer.setOnClickListener {
            handleClickWithAmountLimit("2")
        }

        isw_keypad_three_transfer.setOnClickListener {
            handleClickWithAmountLimit("3")
        }

        isw_keypad_four_transfer.setOnClickListener {
            handleClickWithAmountLimit("4")
        }

        isw_keypad_five_transfer.setOnClickListener {
            handleClickWithAmountLimit("5")
        }

        isw_keypad_six_transfer.setOnClickListener {
            handleClickWithAmountLimit("6")
        }

        isw_keypad_seven_transfer.setOnClickListener {
            handleClickWithAmountLimit("7")
        }

        isw_keypad_eight_transfer.setOnClickListener {
            handleClickWithAmountLimit("8")
        }

        isw_keypad_nine_transfer.setOnClickListener {
            handleClickWithAmountLimit("9")
        }

        isw_dot_button_transfer.setOnClickListener {
            handleClickWithAmountLimit(".")
        }

        isw_back_delete_button_transfer.setOnClickListener {
            if (amount.isNotEmpty()) {
                amount = amount.substring(0 until amount.length - 1)
                updateAmount()
            }
        }

        isw_back_delete_button_transfer.setOnLongClickListener {
            amount = DEFAULT_AMOUNT
            isw_amount.text = amount
            true
        }
    }

    private fun handleClickWithAmountLimit(digit: String) {

            amount+= digit
            updateAmount()
    }

    private fun updateAmount() {
        val cleanString = amount.replace("[$,.]".toRegex(), "")

        val parsed = java.lang.Double.parseDouble(cleanString)
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        val formatted = numberFormat.format(parsed / 100)

        isw_amount_transfer.text = formatted
    }

    companion object {
        @JvmStatic
        fun newInstance() = AmountFragment()
        val TAG = "TRANSFER AMOUNT FRAGMENT"
    }
}