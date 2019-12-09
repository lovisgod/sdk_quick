package com.interswitchng.smartpos.modules.sendmoney


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.settings.AccountFragmentDirections
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.isVisible
import kotlinx.android.synthetic.main.fragment_account_transfer.*
import kotlinx.android.synthetic.main.fragment_account_transfer.backImg
import kotlinx.android.synthetic.main.fragment_account_transfer.isw_proceed
import kotlinx.android.synthetic.main.isw_fragment_bill_payment.*


class AccountTransferFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.fragment_account_transfer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {

        backImg.setOnClickListener {
            navigateUp()
        }

        isw_account_number.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                isw_remarks.visibility = View.GONE
                isw_account_name.visibility = View.GONE
                isw_proceed.isEnabled = false
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        isw_account_name.keyListener = null

        isw_proceed.setOnClickListener {
            if (TextUtils.isEmpty(isw_account_name.text))
                Toast.makeText(context, "Account number not valid", Toast.LENGTH_LONG).show()
            else {
                proceedToPayment()
            }
        }

        val plans = resources.getStringArray(R.array.isw_dummy_banks)
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            plans
        )
        bank_spinner.adapter = adapter
        bank_spinner.onItemSelectedListener =
            object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    DisplayUtils.hideKeyboard(activity as Activity)

                    if (isw_account_number.text.length == 10) {
                        isw_remarks.visibility = View.VISIBLE
                        isw_account_name.visibility = View.VISIBLE
                        isw_proceed.isEnabled = true
                    } else {
                        Toast.makeText(context, "Invalid account number", Toast.LENGTH_LONG).show()
                    }
                }

            }
    }

    private fun proceedToPayment() {
        val payment = PaymentModel()
        payment.type = PaymentModel.TransactionType.CARD_PURCHASE
        val direction =
            AccountTransferFragmentDirections.iswActionIswAccounttransferfragmentToIswFragmentAmount(payment)
        navigate(direction)
    }

    companion object {

        const val TAG = "ECASH FRAGMENT"
    }

}
