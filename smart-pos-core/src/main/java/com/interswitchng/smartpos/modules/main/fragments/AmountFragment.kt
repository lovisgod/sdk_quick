package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_amount.*
import java.text.NumberFormat

class AmountFragment : BaseFragment(TAG) {

    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val payment by lazy { amountFragmentArgs.PaymentModel }

    private val DEFAULT_AMOUNT = "0.00"

    private var amount = EMPTY_STRING

    override val layoutId: Int
        get() = R.layout.isw_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (payment.type == PaymentModel.MakePayment.PRE_AUTHORIZATION) {
            isw_proceed.text = getString(R.string.isw_pre_authorize)
        }
        initializeAmount()
        handleProceedToolbarClicks()
        handleDigitsClicks()
    }

    private fun handleProceedToolbarClicks() {
        isw_proceed.setOnClickListener {
            if (amount == EMPTY_STRING || amount == DEFAULT_AMOUNT) {
                displayInvalidAmountToast()
            } else {
                proceedWithPayment()
            }
        }

        isw_amount_toolbar.setNavigationOnClickListener {
            navigateUp()
        }
    }

    private fun proceedWithPayment() {
        val latestAmount = isw_amount.text.toString()
        val latestAmountWithoutComma = latestAmount.replace("[$,]".toRegex(), "")
        val dotIndex = latestAmountWithoutComma.indexOfFirst {
            it == '.'
        }

        val stringWithoutCommaAndDot =  latestAmountWithoutComma.substring(0, dotIndex)
        payment.newPayment {
            amount = Integer.valueOf(stringWithoutCommaAndDot)
            formattedAmount = latestAmount
        }

        when (payment.type) {
            PaymentModel.MakePayment.PURCHASE -> {
                val bottomDialog = PaymentTypeDialog {


                    when (it) {
                        PaymentModel.PaymentType.QR_CODE -> {
                            payment.newPayment {
                                paymentType = it
                            }

                            val direction = AmountFragmentDirections.iswActionGotoFragmentQrCodeFragment(payment)
                            navigate(direction)
                        }
                        PaymentModel.PaymentType.PAY_CODE -> {
                            payment.newPayment {
                                paymentType = it
                            }

                            val direction = AmountFragmentDirections.iswActionGotoFragmentPayCode(payment)
                            navigate(direction)
                        }
                        PaymentModel.PaymentType.USSD -> {
                            payment.newPayment {
                                paymentType = it
                            }

                            val direction = AmountFragmentDirections.iswActionGotoFragmentUssd(payment)
                            navigate(direction)
                        }

                        PaymentModel.PaymentType.CARD -> {
                            payment.newPayment {
                                paymentType = it
                            }

                            val direction = AmountFragmentDirections.iswActionGotoFragmentCardPayment(payment)
                            navigate(direction)
                        }
                    }
                }
                bottomDialog.show(childFragmentManager, TAG)

            }

            PaymentModel.MakePayment.PRE_AUTHORIZATION -> {
                val direction = AmountFragmentDirections.iswActionGotoFragmentPin(payment)
                navigate(direction)
            }

            PaymentModel.MakePayment.COMPLETION -> {
                val direction =
                    AmountFragmentDirections.iswActionGotoFragmentProcessingTransaction(payment)
                navigate(direction)
            }

            PaymentModel.MakePayment.CARD_NOT_PRESENT -> {

            }
        }
    }

    private fun displayInvalidAmountToast() {
        toast("Enter a valid amount")
    }

    private fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    private fun initializeAmount() {
        amount = DEFAULT_AMOUNT
        isw_amount.text = amount
    }

    private fun updateAmount() {
        val cleanString = amount.replace("[$,.]".toRegex(), "")

        val parsed = java.lang.Double.parseDouble(cleanString)
        val numberFormat = NumberFormat.getInstance()
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2
        val formatted = numberFormat.format(parsed / 100)

        isw_amount.text = formatted
    }

    private fun handleDigitsClicks() {
        isw_keypad_zero.setOnClickListener {
            amount+= "0"
            updateAmount()
        }

        isw_keypad_one.setOnClickListener {
            amount+= "1"
            updateAmount()
        }

        isw_keypad_two.setOnClickListener {
            amount+= "2"
            updateAmount()
        }

        isw_keypad_three.setOnClickListener {
            amount+= "3"
            updateAmount()
        }

        isw_keypad_four.setOnClickListener {
            amount+= "4"
            updateAmount()
        }

        isw_keypad_five.setOnClickListener {
            amount+= "5"
            updateAmount()
        }

        isw_keypad_six.setOnClickListener {
            amount+= "6"
            updateAmount()
        }

        isw_keypad_seven.setOnClickListener {
            amount+= "7"
            updateAmount()
        }

        isw_keypad_eight.setOnClickListener {
            amount+= "8"
            updateAmount()
        }

        isw_keypad_nine.setOnClickListener {
            amount+= "9"
            updateAmount()
        }

        isw_dot_button.setOnClickListener {
            amount+= "."
            updateAmount()
        }

        isw_back_delete_button.setOnClickListener {
            if (amount.isNotEmpty()) {
                amount = amount.substring(0 until amount.length - 1)
                updateAmount()
            }
        }

        isw_back_delete_button.setOnLongClickListener {
            amount = DEFAULT_AMOUNT
            isw_amount.text = amount
            true
        }
    }

    companion object {
        const val TAG = "AMOUNT FRAGMENT"
    }
}