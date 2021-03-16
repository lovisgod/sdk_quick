package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.modules.main.transfer.customdailog
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.toast
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_processing_transaction.*
import kotlinx.android.synthetic.main.isw_fragment_transfertransaction_preocessing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class TransfertransactionPreocessingFragment : BaseFragment(TAG) {
    private val processingRequestFragmentArgs by navArgs<TransfertransactionPreocessingFragmentArgs>()
    private val paymentModel by lazy { processingRequestFragmentArgs.paymentModel }
    private val cardType by lazy { processingRequestFragmentArgs.CardType }
    private val transactionType by lazy { processingRequestFragmentArgs.TransactionType }
    private val accountType by lazy { processingRequestFragmentArgs.AccountType }
    private lateinit var dialog: Dialog

    private lateinit var transactionResult: TransactionResult

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfertransaction_preocessing

    private val cardViewModel: CardViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = Dialog(this.requireContext())
        isw_processing_text_transfer.text =
                getString(R.string.isw_processing_transaction, "Transfer")

        isw_connecting_transfer.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.isw_round_gray_stroke,
                0,
                0,
                0
        )
        isw_authenticating_transfer.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.isw_round_gray_stroke,
                0,
                0,
                0
        )
        isw_receiving_transfer.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.isw_round_gray_stroke,
                0,
                0,
                0
        )

        observeViewModel()

        runWithInternet {
//                dialog = customdailog(this.requireContext())
                cardViewModel.processTransferTransaction(
                        paymentModel,
                        accountType,
                        terminalInfo,
                        Prefs.getString(Constants.SETTLEMENT_ACCOUNT_NUMBER, ""), // use this for generic transfer
                        Prefs.getString(Constants.SETTLEMENT_BANK_CODE, "")  // use this fot specific transfer
                )
            }
    }

    private fun observeViewModel() {
        val owner = { lifecycle }
        with(cardViewModel) {
            transactionResponse.observe(owner) {
                it?.let(::processResponse)
            }
            onlineResult.observe(owner) {
                it?.let { result ->
                    when (result) {
                        CardViewModel.OnlineProcessResult.NO_EMV -> {
                            context?.toast("Unable to getResult icc")
                            //finish()
                        }
                        CardViewModel.OnlineProcessResult.NO_RESPONSE -> {
                            context?.toast("Unable to process Transaction")
                            //finish()
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_DENIED -> {
                            context?.toast("Transaction Declined")
                            //showContainer(CardTransactionState.Default)
                        }
                        //CardViewModel.OnlineProcessResult.ONLINE_APPROVED ->
                        else -> {
//                            isw_connecting_transfer.apply {
//                                setCompoundDrawablesWithIntrinsicBounds(
//                                        R.drawable.isw_round_done,
//                                        0,
//                                        0,
//                                        0
//                                )
//                                text = "Connected"
//
//                                isw_connection_progress_transfer.progress =
//                                        isw_connection_progress_transfer.progress + 30
//                            }

                            isw_status_text_changing.apply {
                                text = "Connected"
                                isw_connection_progress_bar.progress =
                                        isw_connection_progress_bar.progress + 30
                            }

                            runBlocking { delay(1000) }

//                            isw_authenticating_transfer.apply {
//                                setCompoundDrawablesWithIntrinsicBounds(
//                                        R.drawable.isw_round_done,
//                                        0,
//                                        0,
//                                        0
//                                )
//                                text = "Authenticated"
//
//                                isw_connection_progress_transfer.progress =
//                                        isw_connection_progress_transfer.progress + 30
//                            }
                            isw_status_text_changing.apply {
                                text = "Authenticated"
                                isw_connection_progress_bar.progress =
                                        isw_connection_progress_bar.progress + 30
                            }

                            context?.toast("Transaction Approved")
                        }
                    }
                }
            }
        }
    }

    private fun processResponse(transactionResponse: Optional<Pair<TransactionResponse, EmvData>>) {
//        if (dialog.isShowing) { dialog.dismiss() }
        when (transactionResponse) {

            is None -> logger.log("Unable to complete transaction")
            is Some -> {
//                isw_receiving_transfer.apply {
//                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_done, 0, 0, 0)
//                    text = "Received"
//
//                    isw_connection_progress_transfer.progress = isw_connection_progress_transfer.progress + 30
//                }

                isw_status_text_changing.apply {
                    text = "Received"
                    isw_connection_progress_bar.progress =
                            isw_connection_progress_bar.progress + 30
                }
                // extract info
                val response = transactionResponse.value.first
                val emvData = transactionResponse.value.second
                val txnInfo =
                        TransactionInfo.fromEmv(emvData, paymentModel, PurchaseType.Card, accountType)
                val responseMsg: String = IsoUtils.getIsoResultMsg(response.responseCode)
                        ?: response.responseDescription ?: "Unknown Error"

                val pinStatus = when {
                    response.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }

                val now = Date()
                transactionResult = TransactionResult(
                        paymentType = PaymentType.Card,
                        dateTime = DateUtils.universalDateFormat.format(now),
                        amount = paymentModel.amount.toString(),
                        type = response.type,
                        authorizationCode = response.authCode,
                        responseMessage = responseMsg,
                        responseCode = response.responseCode,
                        cardPan = txnInfo.cardPAN,
                        cardExpiry = txnInfo.cardExpiry,
                        cardType = cardType,
                        stan = response.stan,
                        pinStatus = pinStatus,
                        AID = emvData.AID,
                        code = "",
                        telephone = iswPos.config.merchantTelephone,
                        icc = txnInfo.iccString,
                        src = txnInfo.src,
                        csn = txnInfo.csn,
                        cardPin = txnInfo.cardPIN,
                        cardTrack2 = txnInfo.cardTrack2,
                        month = response.month,
                        time = now.time,
                        originalTransmissionDateTime = response.transmissionDateTime,
                        name = response.name, // biller's name for billPayment
                        ref = response.ref,   // biller's transactionReference for billPayment
                        rrn = response.rrn    // biller's retrieval reference number for billPayment
                )

                val direction =
                       TransfertransactionPreocessingFragmentDirections.iswActionIswTransfertransactionpreocessingfragmentToIswTransferstatusfragment(
                                paymentModel,
                                TransactionResponseModel(
                                        transactionResult = transactionResult,
                                        transactionType = PaymentModel.TransactionType.CARD_PURCHASE
                                )
                        )
               findNavController().navigate(direction)

                // delete the saved receivinginstitionid and destinationAccountNumber
                Prefs.remove("receivingInstitutionId")
                Prefs.remove("destinationAccountNumber")
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = TransfertransactionPreocessingFragment()
        val TAG = "TRANSFER TRANSACTION PROCESSING FRAGMENT"
    }
}