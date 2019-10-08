package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.MainActivity
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.errors.NotConfiguredException
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.utilities.addComma
import com.interswitchng.smartpos.shared.utilities.beforeDot
import com.interswitchng.smartpos.shared.utilities.removeComma
import kotlinx.android.synthetic.main.isw_fragment_amount.*

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
        payment.newPayment {
            amount = isw_amount.text.toString()
        }

        when (payment.type) {
            PaymentModel.MakePayment.PURCHASE -> {
                val bottomDialog = PaymentTypeDialog { payment.newPayment {
                    paymentType = it

                    val amountAsInt = isw_amount.text.toString().removeComma().beforeDot().toInt()

                    //makePayment(amountAsInt, getPaymentType(paymentType as PaymentModel.PaymentType))

                    val direction = AmountFragmentDirections.iswActionGotoFragmentCardPayment(payment)
                    navigate(direction)
                } }
                bottomDialog.show(childFragmentManager, TAG)
            }

            PaymentModel.MakePayment.PRE_AUTHORIZATION -> {
                val direction = AmountFragmentDirections.iswActionGotoFragmentPin(payment)
                navigate(direction)
            }

            PaymentModel.MakePayment.COMPLETION -> {
                val direction = AmountFragmentDirections.iswActionGotoFragmentProcessingTransaction(payment)
                navigate(direction)
            }
        }
    }

    private fun getPaymentType(paymentType: PaymentModel.PaymentType): PaymentType {
        return when (paymentType) {
            PaymentModel.PaymentType.CARD -> PaymentType.Card
            PaymentModel.PaymentType.USSD -> PaymentType.USSD
            PaymentModel.PaymentType.QR_CODE -> PaymentType.QR
            PaymentModel.PaymentType.PAY_CODE -> PaymentType.PayCode
        }
    }

    private fun displayInvalidAmountToast() {
        toast("Enter a valid amount")
    }

    private fun makePayment(amount: Int, type: PaymentType?) {
        try {
            // trigger payment
            activity?.let {
                IswPos.getInstance().initiatePayment(it, amount, type)
            }

        } catch (e: NotConfiguredException) {
            val message = "Pos has not been configured"
            toast(message)
            logger.log(e.message ?: e.localizedMessage ?: message)
        }
    }

    private fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    private fun initializeAmount() {
        amount = DEFAULT_AMOUNT
        isw_amount.text = amount
    }

    private fun updateAmount(digit: String) {
        amount += digit

        amount = amount.removeComma()

        val amountBeforeDot = amount.beforeDot()

        if (amountBeforeDot.length >= 4) amount = amount.addComma()

        isw_amount.text = amount
    }

    private fun handleDigitsClicks() {
        isw_keypad_zero.setOnClickListener {
            updateAmount("0")
        }

        isw_keypad_one.setOnClickListener {
            updateAmount("1")
        }

        isw_keypad_one.setOnClickListener {
            updateAmount("1")
        }

        isw_keypad_two.setOnClickListener {
            updateAmount("2")
        }

        isw_keypad_three.setOnClickListener {
            updateAmount("3")
        }

        isw_keypad_four.setOnClickListener {
            updateAmount("4")
        }

        isw_keypad_five.setOnClickListener {
            updateAmount("5")
        }

        isw_keypad_six.setOnClickListener {
            updateAmount("6")
        }

        isw_keypad_seven.setOnClickListener {
            updateAmount("7")
        }

        isw_keypad_eight.setOnClickListener {
            updateAmount("8")
        }

        isw_keypad_nine.setOnClickListener {
            updateAmount("9")
        }

        isw_dot_button.setOnClickListener {
            updateAmount(".")
        }

        isw_back_delete_button.setOnClickListener {
            if (amount.isNotEmpty()) {
                amount = amount.substring(0 until amount.length - 1)

                amount = amount.removeComma()

                val amountBeforeDot = amount.beforeDot()

                if (amountBeforeDot.length >= 4) amount = amount.addComma()

                isw_amount.text = amount
            }

        }

    }

    companion object {
        const val TAG = "AMOUNT FRAGMENT"
    }

    fun navigateToDestination(paymentType: PaymentModel.PaymentType) {
        when (paymentType) {
            PaymentModel.PaymentType.CARD -> {
                val direction = AmountFragmentDirections.iswActionGotoFragmentCardPayment(payment)
                    navigate(direction)
            }

            PaymentModel.PaymentType.QR_CODE -> {

            }

            PaymentModel.PaymentType.PAY_CODE -> {

            }

            PaymentModel.PaymentType.USSD -> {

            }
        }
    }
}