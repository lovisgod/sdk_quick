package com.interswitchng.smartpos.modules.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.FingerprintBottomDialog
import com.interswitchng.smartpos.modules.main.dialogs.MerchantCardDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import kotlinx.android.synthetic.main.isw_fragment_receipt.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReceiptFragment : BaseFragment(TAG) {

    private val receiptFragmentArgs by navArgs<ReceiptFragmentArgs>()
    private val transactionResponseModel by lazy { receiptFragmentArgs.TransactionResponseModel }
    private val result by lazy { transactionResponseModel.transactionResult }
    private val isFromActivityDetail by lazy { receiptFragmentArgs.IsFromActivityDetail }
    private val type by lazy { receiptFragmentArgs.PaymentModel.type }
    private val paymentModel by lazy { receiptFragmentArgs.PaymentModel }

    private val resultViewModel: TransactionResultViewModel by viewModel()

    private var printSlip: TransactionSlip? = null
    private var hasPrintedMerchantCopy = false
    private var hasPrintedCustomerCopy = false
    private var hasClickedReversal = false

    private lateinit var reversalResult: TransactionResult


    override val layoutId: Int
        get() = R.layout.isw_fragment_receipt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpUI()
    }

    private fun displayTransactionResultIconAndMessage() {
        println("Called responseCode ----> ${result?.responseCode}")
        Logger.with("Reciept Fragment").logErr(result?.responseCode.toString())
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
                    PaymentModel.TransactionType.CARD_PURCHASE -> isw_receipt_text.text =
                        getString(R.string.isw_thank_you)
                    PaymentModel.TransactionType.PRE_AUTHORIZATION -> isw_receipt_text.text =
                        getString(R.string.isw_pre_authorization_completed)
                    PaymentModel.TransactionType.CARD_NOT_PRESENT -> isw_receipt_text.text =
                        getString(R.string.isw_card_not_present_completed)
                }
            }

            else -> {
                transactionResponseIcon.setImageResource(R.drawable.isw_failure)
                isw_receipt_text.text = "Failed!"
                isw_transaction_msg.text =
                    result?.responseMessage//"Your transaction was unsuccessful"
            }
        }
    }

    private fun displayTransactionDetails() {
        isw_date_text.text = getString(R.string.isw_receipt_date, result?.dateTime)
        val amountWithCurrency = result?.amount.let { DisplayUtils.getAmountWithCurrency(it.toString()) }
        Logger.with("Reciept fragment").logErr(amountWithCurrency)
        //Logger.with("Recipet fragment amount").logErr(result!!.amount)
        isw_amount_paid.text = getString(R.string.isw_receipt_amount, amountWithCurrency)

        isw_stan.text = result?.stan?.padStart(6,'0')

        val cardTypeName = when (result?.cardType) {
            CardType.MASTER -> "Master Card"
            CardType.VISA -> "Visa Card"
            CardType.VERVE -> "Verve Card"
            CardType.AMERICANEXPRESS -> "American Express Card"
            CardType.CHINAUNIONPAY -> "China Union Pay Card "
            CardType.None -> "Unknown Card"
            else -> "Unknown Card"
        }

        isw_payment_type.text = getString(R.string.isw_receipt_payment_type, cardTypeName)
    }

    private fun logTransaction() {
        result?.let {
            if (isFromActivityDetail.not()) resultViewModel.logTransaction(it)
        }
    }

    private fun handleClicks() {
        try {
//            isw_done.setOnClickListener {
//                val direction = ReceiptFragmentDirections.iswActionIswReceiptfragmentToIswTransaction()
//                val navOptions = NavOptions.Builder()
//                    .setPopUpTo(R.id.isw_transaction, true)
//                    .setLaunchSingleTop(true)
//                    .build()
//                navigate(direction,navOptions)
//            }
        }catch (Ex:Exception){

        }
        try {
            transactionResponseIcon.setOnClickListener {
                val direction =
                    ReceiptFragmentDirections.iswActionGotoFragmentTransaction()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.isw_transaction, true)
                    .setLaunchSingleTop(true)
                    .build()
                navigate(direction, navOptions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isw_share_receipt.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, "")
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, "Select Application"))
        }

        isw_reversal.setOnClickListener { view ->

            authorizeAndPerformAction {

                if (hasClickedReversal.not()) {
                    val now = Date()
                    val txnInfo = TransactionInfo.fromTxnResult(result!!)
                    resultViewModel.initiateReversal(terminalInfo, txnInfo)
                    resultViewModel.transactionResponse.observe(viewLifecycleOwner, Observer<TransactionResponse> {
                        reversalResult = TransactionResult(
                                paymentType = PaymentType.Card,
                                dateTime = DisplayUtils.getIsoString(now),
                                amount = result!!.amount,
                                type = TransactionType.Reversal,
                                authorizationCode = it.authCode,
                                responseMessage = IsoUtils.getIsoResultMsg(it.responseCode)!!,
                                responseCode = it.responseCode,
                                cardPan = txnInfo.cardPAN,
                                cardExpiry = txnInfo.cardExpiry,
                                cardType = result!!.cardType,
                                stan = it.stan,
                                pinStatus = result!!.pinStatus,
                                AID = result!!.AID,
                                code = "",
                                telephone = iswPos.config.merchantTelephone,
                                icc = txnInfo.iccString,
                                src = txnInfo.src,
                                csn = txnInfo.csn,
                                cardPin = txnInfo.cardPIN,
                                cardTrack2 = txnInfo.cardTrack2,
                                month = it.month,
                                time = now.time,
                                originalTransmissionDateTime = it.transmissionDateTime
                        )
                        resultViewModel.logTransaction(reversalResult)
                        Toast.makeText(context, reversalResult.responseMessage, Toast.LENGTH_SHORT).show()
                    })

                    hasClickedReversal = true
                    view.isClickable = false

                }
            }
        }

        isw_refund.setOnClickListener {

            authorizeAndPerformAction {

                paymentModel.type = PaymentModel.TransactionType.REFUND
                val direction = ReceiptFragmentDirections.iswActionGotoFragmentAmount(paymentModel)
                navigate(direction)

            }

        }
    }






    private lateinit var dialog: MerchantCardDialog
    //authorizeAndPerformAction { it.findNavController().navigate(R.id.isw_goto_account_fragment_action) }
    private fun authorizeAndPerformAction(action: () -> Unit) {
        val fingerprintDialog = FingerprintBottomDialog (isAuthorization = true) { isValidated ->
            if (isValidated) {
                action.invoke()
            } else {
                toast("Unauthorized Access!!")
            }
        }

        val alert by lazy {
            DialogUtils.getAlertDialog(requireContext())
                    .setTitle("Invalid Configuration")
                    .setMessage("The configuration contains invalid parameters, please fix the errors and try saving again")
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->

                        dialog.dismiss()
                    }
        }
        dialog = MerchantCardDialog {
            when (it) {
                MerchantCardDialog.AUTHORIZED -> action.invoke()
                MerchantCardDialog.FAILED -> toast("Unauthorized Access!!")
//                MerchantCardDialog.NOT_ENROLLED -> {
//                    alert.setTitle("Supervisor's card not enrolled")
//                    alert.setMessage("You have not yet enrolled a supervisor's card. Please enroll a supervisor's card on the settings page after downloading terminal configuration.")
//                    alert.show()
//
//                }


                MerchantCardDialog.USE_FINGERPRINT -> fingerprintDialog.show(requireFragmentManager(), FingerprintBottomDialog.TAG)
            }
        }
        dialog.show(requireFragmentManager(), MerchantCardDialog.TAG)
    }


    private fun setUpUI() {
        displayTransactionResultIconAndMessage()
        displayTransactionDetails()
        logTransaction()
        displayButtons()
        handleClicks()
        handlePrint()
    }

    private fun displayButtons() {
        if (isFromActivityDetail) {
            when(type) {
                PaymentModel.TransactionType.CARD_PURCHASE -> {
                    isw_reversal.visibility = View.GONE
                    isw_refund.visibility = View.GONE
                }
                else -> {}
            }

            isw_print_receipt.apply {
                visibility = View.VISIBLE
                text = getString(R.string.isw_title_re_print_receipt)
            }

            isw_share_receipt.visibility = View.VISIBLE

        } else {
            isw_print_receipt.visibility = View.VISIBLE
        }

    }

    private fun handlePrint() {
        printSlip = terminalInfo.let { result?.getSlip(it) }

        isw_print_receipt.setOnClickListener {
            // print slip
            printSlip?.let {
                if (!hasPrintedCustomerCopy) {
                    resultViewModel.printSlip(UserType.Customer, it)
                    hasPrintedCustomerCopy = true
                } else if (hasPrintedMerchantCopy) {
                    resultViewModel.printSlip(UserType.Merchant, it, reprint = true)
                } else {
                    // if has not printed merchant copy
                    // print merchant copy
                    resultViewModel.printSlip(UserType.Merchant, it)
                    // change print text to re-print
                    isw_print_receipt.text = getString(R.string.isw_title_re_print_receipt)
                    hasPrintedMerchantCopy = true
                }
            }
        }
    }

    companion object {
        const val TAG = "Receipt Fragment"
    }
}
