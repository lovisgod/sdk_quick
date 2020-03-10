package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.BillPaymentModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import kotlinx.android.synthetic.main.isw_fragment_bill_payment_new.*
import kotlinx.android.synthetic.main.isw_fragment_card_details.isw_card_details_toolbar
import kotlinx.android.synthetic.main.isw_fragment_card_details.isw_proceed

class BillPaymentFragmentNew : BaseFragment(TAG) {
    private val billPaymentFragmentArgs by navArgs<BillPaymentFragmentNewArgs>()
    private val paymentModel by lazy { billPaymentFragmentArgs.PaymentModel }


    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan(), paymentModel.stan, paymentModel.authorizationId)
    }

    override val layoutId: Int
        get() = R.layout.isw_fragment_bill_payment_new

    override fun onResume() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        isw_proceed.setOnClickListener {

            isw_card_details_toolbar.setNavigationOnClickListener { navigateUp() }

            val billPaymentModel = BillPaymentModel {
                customerId = isw_customer_id.text.toString()
                phoneNumber = isw_phone_number.text.toString()
                customerEmail = isw_customer_email.text.toString()
                paymentCode = isw_payment_code.text.toString()
                bankCbnCode = isw_bank_cbn_code.text.toString()
            }

            paymentModel.newPayment {
                  amount = paymentInfo.amount
                  billPayment = billPaymentModel
              }
              if (isw_customer_id.text.toString() == "" || isw_bank_cbn_code.text.toString() == "" || isw_payment_code.text.toString() == "") {
                  toast("Some Fields are empty")
              } else {
                  paymentModel.newPayment {
                      paymentType = PaymentModel.PaymentType.CARD
                  }
                  val direction = AmountFragmentDirections.iswActionGotoFragmentCardTransactions(paymentModel)
                  navigate(direction)
              }

        }
    }

    companion object {
        const val TAG = "Bill Payment Fragment"
    }
}