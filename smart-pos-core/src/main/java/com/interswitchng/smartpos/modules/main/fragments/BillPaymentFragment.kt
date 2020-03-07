package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.BillPaymentModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import kotlinx.android.synthetic.main.isw_fragment_bill_payment_new.*
import kotlinx.android.synthetic.main.isw_fragment_card_details.isw_card_details_toolbar
import kotlinx.android.synthetic.main.isw_fragment_card_details.isw_proceed

class BillPaymentFragment : BaseFragment(TAG) {

    private val billPaymentFragmentArgs by navArgs<BillPaymentFragmentArgs>()
    private val paymentModel by lazy { billPaymentFragmentArgs.PaymentModel }

    private lateinit var transactionResponseModel: TransactionResponseModel
    private var transactionType: TransactionType = TransactionType.CardNotPresent
    private var accountType = AccountType.Default
    private lateinit var transactionResult: TransactionResult
    private var pinOk = false
    //lateinit var terminalInfo:TerminalInfo


    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan(), paymentModel.stan, paymentModel.authorizationId)
    }

    override val layoutId: Int
        get() = R.layout.isw_fragment_bill_payment_new

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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



        isw_proceed.setOnClickListener {
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