package com.interswitchng.smartpos.modules.paycode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.interfaces.library.IKeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printslips.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import kotlinx.android.synthetic.main.isw_activity_pay_code.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import java.util.*

class PayCodeActivity : BaseActivity(), ScanBottomSheet.ScanResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_pay_code)


        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo.amount / 100)
        amountText.text = getString(R.string.isw_amount, amount)
    }


    private val isoService: IsoService by inject()
    private val store: IKeyValueStore by inject()
    private lateinit var transactionResult: TransactionResult

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {
        paymentHint.text = "Type in your Pay Code"
        btnContinue.setOnClickListener {
            btnContinue.isEnabled = false
            btnContinue.isClickable = false
            // start paycode process
            val disposable = ThreadUtils.createExecutor { processOnline() }
            disposables.add(disposable)
        }

        btnScanCode.setOnClickListener {
            ScanBottomSheet
                    .newInstance()
                    .show(supportFragmentManager, ScanBottomSheet.toString())
        }

    }

    private fun processOnline() {
        runOnUiThread {
            // change hint text
            paymentHint.text = getString(R.string.isw_title_processing_transaction)
            // show transaction progress alert
            showProgressAlert()
        }

        // TODO refactor this function [extremely ugly!!]
        TerminalInfo.get(store)?.let { terminalInfo ->
            val code = payCode.text.toString()
            val response = isoService.initiatePaycodePurchase(terminalInfo, code, paymentInfo)
            // used default transaction because the
            // transaction is not processed by isw directly
            val txn = Transaction.default()

            val now = Date()
            response?.let {

                val responseMsg = IsoUtils.getIsoResultMsg(it.responseCode) ?: "Unknown Error"

                transactionResult = TransactionResult(
                        paymentType = PaymentType.Card,
                        dateTime = DisplayUtils.getIsoString(now),
                        amount = DisplayUtils.getAmountString(paymentInfo.amount),
                        type = TransactionType.Purchase,
                        authorizationCode = response.authCode,
                        responseMessage = responseMsg,
                        responseCode = response.responseCode,
                        stan = response.stan, pinStatus = "", AID = "", code = "",
                        cardPan = "", cardExpiry = "", cardType = "",
                        telephone = "08031150978")

                // show transaction result screen
                showTransactionResult(txn)
            } ?: toast("Unable to process Transaction").also { finish() }

        } ?: toast("No terminal info, found on device").also { finish() }
    }

    override fun getTransactionResult(transaction: Transaction) = transactionResult


    override fun onScanComplete(result: String) {
        payCode.setText(result)
        btnContinue.performClick()
    }

    override fun onScanError(code: Int) {
        // TODO handle code response

    }

}
