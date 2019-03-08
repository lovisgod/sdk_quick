package com.interswitchng.smartpos.modules.paycode

import android.os.Bundle
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
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
        val amount = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_amount, amount)
    }


    private val isoService: IsoService by inject()
    private val store: KeyValueStore by inject()
    private lateinit var transactionResult: TransactionResult

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {
        paymentHint.text = "Type in your Pay Code"
        btnContinue.setOnClickListener {
            // disable buttons
            btnContinue.isEnabled = false
            btnContinue.isClickable = false

            // hide keyboard
            DisplayUtils.hideKeyboard(this)

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
            showProgressAlert(false)
        }

        val code = payCode.text.toString()
        val response = isoService.initiatePaycodePurchase(terminalInfo, code, paymentInfo)
        // used default transaction because the
        // transaction is not processed by isw directly
        val txn = Transaction.default()

        val now = Date()
        response?.let {

            val responseMsg = IsoUtils.getIsoResultMsg(it.responseCode) ?: "Unknown Error"

            transactionResult = TransactionResult(
                    paymentType = PaymentType.PayCode,
                    dateTime = DisplayUtils.getIsoString(now),
                    amount = DisplayUtils.getAmountString(paymentInfo),
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
