package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.fragments.ReceiptFragmentArgs
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.hide
import com.interswitchng.smartpos.modules.main.transfer.reveal
import com.interswitchng.smartpos.modules.main.transfer.showSnack
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import kotlinx.android.synthetic.main.isw_fragment_receipt.*
import kotlinx.android.synthetic.main.isw_fragment_transfer_status.*
import org.koin.android.viewmodel.ext.android.viewModel

class TransferStatusFragment() : BaseFragment(TAG) {

    private val receiptFragmentArgs by navArgs<TransferStatusFragmentArgs>()
    private val transactionResponseModel by lazy { receiptFragmentArgs.transactionResponse }
    private val result by lazy { transactionResponseModel.transactionResult }
    private val isFromActivityDetail by lazy {false }
    private val type by lazy { receiptFragmentArgs.paymentModel.type }
    private val paymentModel by lazy { receiptFragmentArgs.paymentModel }

    private val resultViewModel: TransactionResultViewModel by viewModel()

    private var printSlip: TransactionSlip? = null
    private var hasPrintedMerchantCopy = false
    private var hasPrintedCustomerCopy = false
    private var hasClickedReversal = false

    private lateinit var reversalResult: TransactionResult

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_status

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        listenToviewModel()
    }

    private fun listenToviewModel() {
        val owner = { lifecycle }
        with(resultViewModel) {
            printerMessage.observe(owner) {
                showSnack(isw_amount_paid_label_transfer, it.toString())
            }
        }
    }


    private fun setUpUI() {
        displayTransactionResultIconAndMessage()
        displayTransactionDetails()
//        logTransaction()
        displayButtons()
        handleClicks()
        handlePrint()
    }

    private fun handlePrint() {
        printSlip = terminalInfo.let { result?.getSlip(it) }

        isw_print_receipt_transfer.setOnClickListener {
            // print slip
            printSlip?.let {
                if (result?.hasPrintedCustomerCopy == 0) {
                    resultViewModel.printSlip(UserType.Customer, it)
                    result?.hasPrintedCustomerCopy = 1
                    resultViewModel.updateTransaction(result!!)
                } else if (result?.hasPrintedMerchantCopy == 1) {
                    resultViewModel.printSlip(UserType.Merchant, it, reprint = true)
                } else {
                    // if has not printed merchant copy
                    // print merchant copy
                    resultViewModel.printSlip(UserType.Merchant, it)
                    // change print text to re-print
                    isw_print_receipt_transfer.text = getString(R.string.isw_title_re_print_receipt)
                    result?.hasPrintedMerchantCopy = 1
                    resultViewModel.updateTransaction(result!!)
                }
            }
        }
    }

    private fun handleClicks() {
       transactionResponseIcon_transfer.setOnClickListener {
           findNavController().popBackStack(R.id.isw_transferlandingfragment, false)
       }

        isw_go_to_landing.setOnClickListener {
            findNavController().popBackStack(R.id.isw_transferlandingfragment, false)
        }

        isw_share_receipt_transfer.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, "")
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, "Select Application"))
        }

    }

    private fun displayButtons() {
       isw_share_receipt_transfer.hide()
       isw_print_receipt_transfer.reveal()
    }

    private fun logTransaction() {
        TODO("Not yet implemented")
        // this will be handled by resultViewModel
    }

    private fun displayTransactionDetails() {
        isw_date_text_transfer.text = getString(R.string.isw_receipt_date, result?.dateTime)
        val amountWithCurrency = result?.amount.let { DisplayUtils.getAmountWithCurrency(it.toString()) }
        Logger.with("Reciept fragment").logErr(amountWithCurrency)
        //Logger.with("Recipet fragment amount").logErr(result!!.amount)
        isw_amount_paid_transfer.text = getString(R.string.isw_receipt_amount, amountWithCurrency)

        isw_stan_transfer.text = result?.stan?.padStart(6, '0')

        val cardTypeName = when (result?.cardType) {
            CardType.MASTER -> "Master Card"
            CardType.VISA -> "Visa Card"
            CardType.VERVE -> "Verve Card"
            CardType.AMERICANEXPRESS -> "American Express Card"
            CardType.CHINAUNIONPAY -> "China Union Pay Card "
            CardType.None -> "Unknown Card"
            else -> "Unknown Card"
        }

        isw_payment_type_transfer.text = getString(R.string.isw_receipt_payment_type, cardTypeName)
    }

    private fun displayTransactionResultIconAndMessage() {
        println("Called responseCode ----> ${result?.responseCode}")
        Logger.with("Reciept Fragment").logErr(result?.responseCode.toString())
        when (result?.responseCode) {
            IsoUtils.TIMEOUT_CODE -> {
                transactionResponseIcon_transfer.setImageResource(R.drawable.isw_failure)
                transactionstatus_image.setImageResource(R.drawable.ic_not_successful)
                isw_go_to_landing.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.iswTextColorError))
                isw_receipt_text_transfer.text = "Failed!"
                isw_transaction_msg_transfer.text = "Your transaction was unsuccessful"
            }

            IsoUtils.OK -> {
                transactionResponseIcon_transfer.setImageResource(R.drawable.isw_round_done_padded)
                transactionstatus_image.setImageResource(R.drawable.ic_finished)
                isw_go_to_landing.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.iswTextColorSuccessDark))
                isw_transaction_msg_transfer.text = "Your transaction was successful"
                isw_receipt_text_transfer.text =
                        getString(R.string.isw_transfer_completed)
            }

            else -> {
                transactionResponseIcon_transfer.setImageResource(R.drawable.isw_failure)
                transactionstatus_image.setImageResource(R.drawable.ic_not_successful)
                isw_go_to_landing.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.iswTextColorError))
                isw_receipt_text_transfer.text = "Failed!"
                isw_transaction_msg_transfer.text =
                        result?.responseMessage//"Your transaction was unsuccessful"
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferStatusFragment()
        val TAG = "TRANSFER STATUS FRAGMENT"
    }


}