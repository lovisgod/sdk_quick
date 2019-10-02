package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.addComma
import com.interswitchng.smartpos.shared.utilities.removeComma
import kotlinx.android.synthetic.main.isw_fragment_amount.*
import kotlinx.android.synthetic.main.isw_fragment_amount.isw_amount
import kotlinx.android.synthetic.main.isw_fragment_amount.isw_amount_toolbar
import kotlinx.android.synthetic.main.isw_fragment_pin.*
import java.util.*

class AmountFragment : BaseFragment(TAG) {

    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val payment by lazy { amountFragmentArgs.PaymentModel }

    private var amount = EMPTY_STRING

    override val layoutId: Int
        get() = R.layout.isw_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeAmount()
        handleProceedToolbarClicks()
        handleDigitsClicks()
    }

    companion object {
        const val TAG = "AMOUNT FRAGMENT"
    }

    private fun handleProceedToolbarClicks() {
        isw_proceed.setOnClickListener {
            payment.newPayment {
                amount = isw_amount.text.toString()
            }
            val bottomDialog = PaymentTypeDialog { payment.newPayment {
                paymentType = it
                val direction = AmountFragmentDirections.iswActionGotoFragmentCardPayment(payment)
                navigate(direction)
            } }

            bottomDialog.show(childFragmentManager, TAG)
        }

        isw_amount_toolbar.setNavigationOnClickListener {
            navigateUp()
        }
    }

    private fun initializeAmount() {
        isw_amount.text = amount
    }

    private fun updateAmount(digit: String) {
        amount += digit

        amount = amount.removeComma()

        if (amount.length >= 4) amount = amount.addComma()

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

                if (amount.length >= 4) amount = amount.addComma()

                isw_amount.text = amount
            }

        }

    }
}