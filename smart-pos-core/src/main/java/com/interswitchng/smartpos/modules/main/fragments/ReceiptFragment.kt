package com.interswitchng.smartpos.modules.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import kotlinx.android.synthetic.main.isw_fragment_receipt.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReceiptFragment : BaseFragment(TAG) {

    private val receiptFragmentArgs by navArgs<ReceiptFragmentArgs>()
    private val transactionResponseModel by lazy { receiptFragmentArgs.TransactionResponseModel }
    private val result by lazy { transactionResponseModel.transactionResult }

    private val resultViewModel: TransactionResultViewModel by viewModel()

    private var printSlip: TransactionSlip? = null
    private var hasPrintedMerchantCopy = false
    private var hasPrintedCustomerCopy = false


    override val layoutId: Int
        get() = R.layout.isw_fragment_receipt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpUI()
    }

    private fun displayTransactionResultIconAndMessage() {
        println("Called responseCode ----> ${result?.responseCode}")
        when (result?.responseCode) {
            IsoUtils.TIMEOUT_CODE -> {
                transactionResponseIcon.setImageResource(R.drawable.isw_failure)
                isw_receipt_text.text = "Failed!"
                isw_transaction_msg.text = "Your transaction was unsuccessful"
            }

            IsoUtils.OK -> {
                transactionResponseIcon.setImageResource(R.drawable.isw_round_done_padded)
                isw_transaction_msg.text = "Your transaction was successful"
                when (transactionResponseModel.transactionType) {
                    PaymentModel.TransactionType.CARD_PURCHASE -> isw_receipt_text.text = getString(R.string.isw_thank_you)
                    PaymentModel.TransactionType.PRE_AUTHORIZATION -> isw_receipt_text.text = getString(R.string.isw_pre_authorization_completed)
                    PaymentModel.TransactionType.CARD_NOT_PRESENT -> isw_receipt_text.text = getString(R.string.isw_card_not_present_completed)
                }
            }

            else -> {
                transactionResponseIcon.setImageResource(R.drawable.isw_failure)
                isw_receipt_text.text = "Failed!"
                isw_transaction_msg.text = result?.responseMessage//"Your transaction was unsuccessful"
            }
        }
    }

    private fun displayTransactionDetails() {
        isw_date_text.text = result?.dateTime
        isw_amount_paid.text = result?.amount

        val cardTypeName = when (result?.cardType) {
            CardType.MASTER -> "Master Card"
            CardType.VISA -> "Visa Card"
            CardType.VERVE -> "Verve Card"
            CardType.AMERICANEXPRESS -> "American Express Card"
            CardType.CHINAUNIONPAY -> "China Union Pay Card "
            CardType.None -> "Unknown Card"
            else -> "Unknown Card"
        }

        isw_payment_type.text = cardTypeName
    }

    private fun logTransaction() {
        result?.let {
            //resultViewModel.logTransaction(it)
        }
    }

    private fun handleClicks() {
        isw_share_receipt.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, "")
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, "Select Application"))
        }

        isw_done.setOnClickListener {
            val direction = ReceiptFragmentDirections.iswActionIswReceiptfragmentToIswTransaction()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.isw_transaction, true)
                .setLaunchSingleTop(true)
                .build()
            navigate(direction, navOptions)
        }

        isw_reversal.setOnClickListener {
            val txnInfo = TransactionInfo.fromTxnResult(result!!)
            resultViewModel.initiateReversal(terminalInfo, txnInfo)
        }

    }

    private fun setUpUI() {
        displayTransactionResultIconAndMessage()
        displayTransactionDetails()
        logTransaction()
        handleClicks()
        handlePrint()
    }

    private fun handlePrint() {
        printSlip = terminalInfo.let { result?.getSlip(it) }

        isw_print_receipt.setOnClickListener {
            // print slip
            printSlip?.let {
                if (!hasPrintedCustomerCopy) {
                    resultViewModel.printSlip(UserType.Customer, it)
                }
                else if (hasPrintedMerchantCopy) {
                    resultViewModel.printSlip(UserType.Merchant, it, reprint = true)
                }
                else {
                    // if has not printed merchant copy
                    // print merchant copy
                    resultViewModel.printSlip(UserType.Merchant, it)
                    // change print text to re-print
                    //printBtn.text = getString(R.string.isw_title_re_print_receipt)
                }
            }
        }
    }

    companion object {
        const val TAG = "Receipt Fragment"
    }
}