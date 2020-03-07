package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_processing_transaction.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class ProcessingRequestFragment : BaseFragment(TAG) {

    private val processingRequestFragmentArgs by navArgs<ProcessingRequestFragmentArgs>()
    private val paymentModel by lazy { processingRequestFragmentArgs.PaymentModel }
    private val cardType by lazy { processingRequestFragmentArgs.CardType }
    private val transactionType by lazy { processingRequestFragmentArgs.TransactionType }
    private val accountType by lazy { processingRequestFragmentArgs.AccountType }

    private lateinit var transactionResult: TransactionResult

    override val layoutId: Int
        get() = R.layout.isw_fragment_processing_transaction

    private val cardViewModel: CardViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (paymentModel.type) {
            PaymentModel.TransactionType.CARD_PURCHASE -> isw_processing_text.text =
                    getString(R.string.isw_processing_transaction, "Transaction")
            PaymentModel.TransactionType.PRE_AUTHORIZATION -> isw_processing_text.text =
                    getString(R.string.isw_processing_transaction, "Pre-Authorization")
            PaymentModel.TransactionType.CARD_NOT_PRESENT -> isw_processing_text.text =
                    getString(R.string.isw_processing_transaction, "Card-Not-Present")
            else -> isw_processing_text.text =
                    getString(R.string.isw_processing_transaction, "Completion")
        }
        isw_connecting.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.isw_round_gray_stroke,
                0,
                0,
                0
        )
        isw_authenticating.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.isw_round_gray_stroke,
                0,
                0,
                0
        )
        isw_receiving.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.isw_round_gray_stroke,
                0,
                0,
                0
        )

        observeViewModel()

        runWithInternet {
            if (paymentModel.type == PaymentModel.TransactionType.CARD_NOT_PRESENT) {
                cardViewModel.processOnlineCNP(
                        paymentModel,
                        accountType,
                        terminalInfo,
                        paymentModel.card!!.expiryDate!!,
                        paymentModel.card!!.cardPan!!
                )
            } else {
                cardViewModel.processOnline(paymentModel, accountType, terminalInfo)
            }
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
                        CardViewModel.OnlineProcessResult.ONLINE_APPROVED -> {
                            isw_connecting.apply {
                                setCompoundDrawablesWithIntrinsicBounds(
                                        R.drawable.isw_round_done,
                                        0,
                                        0,
                                        0
                                )
                                text = "Connected"

                                isw_connection_progress.progress =
                                        isw_connection_progress.progress + 30
                            }

                            runBlocking { delay(1000) }

                            isw_authenticating.apply {
                                setCompoundDrawablesWithIntrinsicBounds(
                                        R.drawable.isw_round_done,
                                        0,
                                        0,
                                        0
                                )
                                text = "Authenticated"

                                isw_connection_progress.progress =
                                        isw_connection_progress.progress + 30
                            }

                            context?.toast("Transaction Approved")
                        }
                    }
                }
            }
        }
    }

    private fun processResponse(transactionResponse: Optional<Pair<TransactionResponse, EmvData>>) {

        when (transactionResponse) {
            is None -> logger.log("Unable to complete transaction")
            is Some -> {
                isw_receiving.apply {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_done, 0, 0, 0)
                    text = "Received"

                    isw_connection_progress.progress = isw_connection_progress.progress + 30
                }
                // extract info
                val response = transactionResponse.value.first
                val emvData = transactionResponse.value.second
                val txnInfo =
                        TransactionInfo.fromEmv(emvData, paymentModel, PurchaseType.Card, accountType)

                val responseMsg = IsoUtils.getIsoResultMsg(response.responseCode) ?: "Unknown Error"
                val pinStatus = when {
                    response.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }

                val now = Date()
                transactionResult = TransactionResult(
                        paymentType = PaymentType.Card,
                        dateTime = DisplayUtils.getIsoString(now),
                        amount = paymentModel.amount.toString(),
                        type = transactionType,
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
                        time = response.time,
                        originalTransmissionDateTime = response.transmissionDateTime
                )

                val direction =
                        ProcessingRequestFragmentDirections.iswActionIswFragmentProcessingTransactionToIswReceiptFragment(
                                paymentModel,
                                TransactionResponseModel(
                                        transactionResult = transactionResult,
                                        transactionType = PaymentModel.TransactionType.CARD_PURCHASE
                                ), false
                        )
                navigate(direction)
            }
        }
    }

    companion object {
        const val TAG = "Processing Request Fragment"
    }
}