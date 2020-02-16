package com.interswitchng.smartpos.modules.bills.fragments


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.fragments.AmountFragment
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_bill_payment.*


class BillPaymentFragment : BaseFragment(TAG) {

    private val billPaymentFragmentArgs by navArgs<BillPaymentFragmentArgs>()
    private val bill by lazy { billPaymentFragmentArgs.bill }

    override val layoutId: Int
        get() = R.layout.isw_fragment_bill_payment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()

    }

    private fun initializeViews() {
        isw_fragment_title.text = bill.name

        isw_reference_amounttext.keyListener = null

        val plans = resources.getStringArray(R.array.isw_dummy_plans)
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            plans
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val amountArray = resources.getStringArray(R.array.isw_dummy_plan_prices)
                val amountString = String.format(getString(R.string.isw_currency_amount), amountArray[position])
                isw_reference_amounttext.setText(amountString)
                isw_reference_amounttext.visibility = View.VISIBLE
            }

        }

        backImg.setOnClickListener {
            navigateUp()
        }



        isw_proceed.setOnClickListener {
            if (TextUtils.isEmpty(isw_reference_id_text.text))
                Toast.makeText(context, "Please enter your reference number", Toast.LENGTH_LONG).show()
            else {
                proceedToPayment()
            }

        }
    }

    private fun proceedToPayment() {
        val payment = PaymentModel()
        val latestAmount = isw_reference_amounttext.text.toString().trim()
        val sureAmount = latestAmount.replace(getString(R.string.isw_currency_naira), "").trim()
        val latestAmountWithoutComma = latestAmount.replace("[$,]".toRegex(), "")
        val dotIndex = latestAmountWithoutComma.indexOfFirst {
            it == '.'
        }

        //val stringWithoutCommaAndDot =  latestAmountWithoutComma.substring(0, dotIndex)
        payment.newPayment {
            amount = Integer.valueOf(sureAmount)
            formattedAmount = latestAmount
        }

        val bottomDialog = PaymentTypeDialog {
            when (it) {
                PaymentModel.PaymentType.QR_CODE -> {
                    payment.paymentType = it

                    val direction =
                        BillPaymentFragmentDirections.iswActionIswBillpaymentfragment2ToIswFragmentQrCode(payment)
                    navigate(direction)
                }
                PaymentModel.PaymentType.PAY_CODE -> {
                    payment.paymentType = it

                    val direction =
                        BillPaymentFragmentDirections.iswActionIswBillpaymentfragment2ToIswFragmentPayCode(payment)
                    navigate(direction)
                }
                PaymentModel.PaymentType.USSD -> {
                    payment.paymentType = it

                    val direction =
                        BillPaymentFragmentDirections.iswActionIswBillpaymentfragment2ToIswUssdFragment(payment)
                    navigate(direction)
                }

                PaymentModel.PaymentType.CARD -> {
                    payment.paymentType = it

                    val direction =
                        BillPaymentFragmentDirections.iswActionIswBillpaymentfragment2ToIswFragmentCardTransactions(
                            payment
                        )
                    navigate(direction)
                }
            }
        }
        bottomDialog.show(childFragmentManager, AmountFragment.TAG)

    }

    companion object {
        const val TAG = "BILL PAYMENT FRAGMENT"
    }
}
