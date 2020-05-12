package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.FingerprintBottomDialog
import com.interswitchng.smartpos.modules.main.dialogs.MakePaymentDialog
import com.interswitchng.smartpos.modules.main.dialogs.MerchantCardDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.payment
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_transaction.*
import java.text.DateFormat
import java.util.*

class TransactionFragment: BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_date.text = DateFormat.getDateInstance(DateFormat.LONG).format(Date(System.currentTimeMillis()))
        isw_merchant_name.text = "SmartWare Solutions"
        //isw_bill_payment_card.setOnClickListener{TransactionFragmentDirections.iswActionGotoFragmentBills()}
        isw_make_payment_card.setOnClickListener {
            val sheet = MakePaymentDialog {
                when (it) {
                    0 -> {
                        val payment = payment {
                            type = PaymentModel.TransactionType.CARD_PURCHASE
                        }
                        navigate(TransactionFragmentDirections.iswActionGotoFragmentAmount(payment))
                    }
                    1 -> {
                        val fingerprintDialog = FingerprintBottomDialog(isAuthorization = true) { isValidated ->
                            if (isValidated) {
                                val payment = payment {
                                    type = PaymentModel.TransactionType.PRE_AUTHORIZATION
                                }
                                navigate(TransactionFragmentDirections.iswActionGotoFragmentAmount(payment))
                            } else {
                                toast("Fingerprint Verification Failed!!")
                                navigateUp()
                            }
                        }
                        val dialog = MerchantCardDialog { type ->
                            when (type) {
                                MerchantCardDialog.AUTHORIZED -> {
                                    val payment = payment {
                                        this.type = PaymentModel.TransactionType.PRE_AUTHORIZATION
                                    }
                                    navigate(TransactionFragmentDirections.iswActionGotoFragmentAmount(payment))
                                }
                                MerchantCardDialog.FAILED -> {
                                    toast("Merchant Card Verification Failed!!")
                                    navigateUp()
                                }
                                MerchantCardDialog.USE_FINGERPRINT -> {
                                    fingerprintDialog.show(childFragmentManager, FingerprintBottomDialog.TAG)
                                }
                            }
                        }
                        dialog.show(childFragmentManager, MerchantCardDialog.TAG)

                    }
//                    2 -> {
//                        val payment = payment {
//                            type = PaymentModel.TransactionType.CARD_NOT_PRESENT
//                        }
//                        val fingerprintDialog = FingerprintBottomDialog (isAuthorization = true) { isValidated ->
//                            if (isValidated) {
//                                val direction = TransactionFragmentDirections.iswActionGotoFragmentCardDetails(payment)
//                                navigate(direction)
//                            } else {
//                                toast("Fingerprint Verification Failed!!")
//                                navigateUp()
//                            }
//                        }
//                        val dialog = MerchantCardDialog { type ->
//                            when (type) {
//                                MerchantCardDialog.AUTHORIZED -> {
//                                    val direction = TransactionFragmentDirections.iswActionGotoFragmentCardDetails(payment)
//                                    navigate(direction)
//                                }
//                                MerchantCardDialog.FAILED -> {
//                                    toast("Merchant Card Verification Failed!!")
//                                    navigateUp()
//                                }
//                                MerchantCardDialog.USE_FINGERPRINT -> {
//                                    fingerprintDialog.show(childFragmentManager, FingerprintBottomDialog.TAG)
//                                }
//                            }
//                        }
//                        dialog.show(childFragmentManager, MerchantCardDialog.TAG)
//                    }
                    3 -> {
                        val payment = payment {
                            type = PaymentModel.TransactionType.COMPLETION
                        }
                        
                        val direction = TransactionFragmentDirections.iswActionGotoFragmentAuthentication(payment)
                        navigate(direction)
                    }
                    4 -> {
                        val payment = payment {
                            type = PaymentModel.TransactionType.BILL_PAYMENT
                        }
                        navigate(TransactionFragmentDirections.iswActionGotoFragmentAmount(payment))
                    }
                    5 -> {
                        val fingerprintDialog = FingerprintBottomDialog(isAuthorization = true) { isValidated ->
                            if (isValidated) {
                                val payment = payment {
                                    type = PaymentModel.TransactionType.REFUND
                                }
                                navigate(TransactionFragmentDirections.iswActionGotoFragmentAmount(payment))
                            } else {
                                toast("Fingerprint Verification Failed!!")
                                navigateUp()
                            }
                        }
                        val dialog = MerchantCardDialog { type ->
                            when (type) {
                                MerchantCardDialog.AUTHORIZED -> {
                                    val payment = payment {
                                        this.type = PaymentModel.TransactionType.REFUND
                                    }
                                    navigate(TransactionFragmentDirections.iswActionGotoFragmentAmount(payment))
                                }
                                MerchantCardDialog.FAILED -> {
                                    toast("Merchant Card Verification Failed!!")
                                    navigateUp()
                                }
                                MerchantCardDialog.USE_FINGERPRINT -> {
                                    fingerprintDialog.show(childFragmentManager, FingerprintBottomDialog.TAG)
                                }
                            }
                        }
                        dialog.show(childFragmentManager, MerchantCardDialog.TAG)

                    }

                }
            }
            sheet.show(childFragmentManager, MakePaymentDialog.TAG)
        }
        isw_bill_payment_card.setOnClickListener {
            val direction = TransactionFragmentDirections.iswActionIswTransactionToIswBillscategoryfragment2()
            navigate(direction)
        }
        isw_transfer_money_card.setOnClickListener {
            val direction = TransactionFragmentDirections.iswActionIswTransactionToIswSendmoneyfragment()
            navigate(direction)
        }

        isw_cash_out_card.setOnClickListener {
            val payment = PaymentModel()
            payment.type = PaymentModel.TransactionType.CARD_PURCHASE
            val direction = TransactionFragmentDirections.iswActionGotoFragmentAmount(payment)
            navigate(direction)
        }
    }

    companion object {
        const val TAG = "TRANSACTION FRAGMENT"
    }
}
