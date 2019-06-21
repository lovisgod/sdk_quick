package com.interswitchng.smartpos.modules.paycode

import android.os.Bundle
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_activity_pay_code.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.viewmodel.ext.android.viewModel

class PayCodeActivity : BaseActivity(), ScanBottomSheet.ScanResultCallback {

    private val payCodeViewModel: PayCodeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_pay_code)


        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_amount, amount)
    }

    private lateinit var transactionResult: TransactionResult

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {
        paymentHint.text = "Type in your Pay Code"
        btnContinue.setOnClickListener {

            if (payCode.text.isNullOrEmpty() || payCode.text.isNullOrBlank()) {
                toast("A Paycode is required")
                return@setOnClickListener
            }

            // disable buttons
            btnContinue.isEnabled = false
            btnContinue.isClickable = false

            // disable scan button
            btnScanCode.isClickable = false
            btnScanCode.isEnabled = false

            // hide keyboard
            DisplayUtils.hideKeyboard(this)

            // start paycode process
            processOnline()

        }

        btnScanCode.setOnClickListener {
            ScanBottomSheet
                    .newInstance()
                    .show(supportFragmentManager, ScanBottomSheet.toString())
        }

    }

    private fun processOnline() {

        // change hint text
        paymentHint.text = getString(R.string.isw_title_processing_transaction)
        // show transaction progress alert
        showProgressAlert(false)

        // observe result
        payCodeViewModel.transactionResult.observe({lifecycle}) {
            it?.apply {
                when (this) {
                    is None -> toast("Unable to process Transaction").also { finish() }
                    is Some -> {
                        // set result
                        transactionResult = value
                        // use default transaction because the
                        // transaction is not need for result
                        val txn = Transaction.default()
                        // show transaction result screen
                        showTransactionResult(txn)
                    }
                }
            }
        }

        // get the paycode
        val code = payCode.text.toString()
        // initiate paycode purchase
        payCodeViewModel.processOnline(terminalInfo, code, paymentInfo)
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
