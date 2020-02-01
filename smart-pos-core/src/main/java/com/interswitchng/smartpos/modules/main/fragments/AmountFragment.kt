package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.MainActivity
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.isw_fragment_amount.*
import java.text.NumberFormat

const val AMOUNT_LIMIT = 10000000
class AmountFragment : BaseFragment(TAG) {

    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val payment by lazy { amountFragmentArgs.PaymentModel }

    private val DEFAULT_AMOUNT = "0.00"

    private var amount = EMPTY_STRING

    override val layoutId: Int
        get() = R.layout.isw_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpUI()
        initializeAmount()
        handleProceedToolbarClicks()
        handleDigitsClicks()
    }

    private fun setUpUI() {
        DisplayUtils.hideKeyboard(activity as MainActivity)
        when (payment.type) {
            PaymentModel.TransactionType.PRE_AUTHORIZATION -> {
                isw_proceed.text = getString(R.string.isw_pre_authorize)
            }

            PaymentModel.TransactionType.REFUND -> {
                isw_proceed.text = getString(R.string.isw_refund)
            }
        }
    }

    private fun handleProceedToolbarClicks() {
        isw_proceed.setOnClickListener {
            if (amount == EMPTY_STRING || amount == DEFAULT_AMOUNT) {
                displayInvalidAmountToast()
            } else {
                proceedWithPayment()
            }
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
            PaymentModel.TransactionType.CARD_PURCHASE -> {
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

                            val direction = AmountFragmentDirections.iswActionGotoFragmentCardTransactions(payment)
                            navigate(direction)
                        }
                    }
                }
                bottomDialog.show(childFragmentManager, TAG)

            }

            PaymentModel.TransactionType.PRE_AUTHORIZATION -> {
                val direction = AmountFragmentDirections.iswActionGotoFragmentCardTransactions(payment)
                navigate(direction)
            }

            PaymentModel.TransactionType.COMPLETION -> {
               val direction =
                    AmountFragmentDirections.iswActionGotoFragmentCardTransactions(payment)
                navigate(direction)
            }

            PaymentModel.TransactionType.REFUND -> {
                val direction =
                    AmountFragmentDirections.iswActionGotoFragmentCardTransactions(payment)
                navigate(direction)
            }

            PaymentModel.TransactionType.ECHANGE -> {
                val direction = AmountFragmentDirections.iswActionIswFragmentAmountToIswPinfragment(payment)
                navigate(direction)
            }

            PaymentModel.TransactionType.ECASH -> {
                val direction = AmountFragmentDirections.iswActionIswFragmentAmountToIswPinfragment(payment)
                navigate(direction)
            }
        }
    }

    private fun displayInvalidAmountToast() {
        toast("Enter a valid amount")
    }

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

    private fun isAmountLimit(): Boolean {
        val cleanString = amount.replace("[$,.]".toRegex(), "")
        val parsed = java.lang.Double.parseDouble(cleanString)
        val parsedAmount = parsed/100

        return parsedAmount > AMOUNT_LIMIT
    }

    private fun handleClickWithAmountLimit(digit: String) {
        if(!isAmountLimit()) {
            amount+= digit
            updateAmount()
        } else {
            toast("Limit is $AMOUNT_LIMIT")
        }
    }

    private fun handleDigitsClicks() {
        isw_keypad_zero.setOnClickListener {
            handleClickWithAmountLimit("0")
        }

        isw_keypad_one.setOnClickListener {
            handleClickWithAmountLimit("1")
        }

        isw_keypad_two.setOnClickListener {
            handleClickWithAmountLimit("2")
        }

        isw_keypad_three.setOnClickListener {
            handleClickWithAmountLimit("3")
        }

        isw_keypad_four.setOnClickListener {
            handleClickWithAmountLimit("4")
        }

        isw_keypad_five.setOnClickListener {
            handleClickWithAmountLimit("5")
        }

        isw_keypad_six.setOnClickListener {
            handleClickWithAmountLimit("6")
        }

        isw_keypad_seven.setOnClickListener {
            handleClickWithAmountLimit("7")
        }

        isw_keypad_eight.setOnClickListener {
            handleClickWithAmountLimit("8")
        }

        isw_keypad_nine.setOnClickListener {
            handleClickWithAmountLimit("9")
        }

        isw_dot_button.setOnClickListener {
            handleClickWithAmountLimit(".")
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