
package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.paycode.PayCodeViewModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_activity_pay_code.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import kotlinx.android.synthetic.main.isw_fragment_paycode.*
import org.koin.android.viewmodel.ext.android.viewModel

class PayCodeFragment : BaseFragment(TAG) {
    private val payCodeViewModel: PayCodeViewModel by viewModel()
    private lateinit var transactionResult: TransactionResult

    override val layoutId: Int
        get() = R.layout.isw_fragment_paycode

    private val payCodeFragmentArgs by navArgs<PayCodeFragmentArgs>()
    private val paymentModel by lazy { payCodeFragmentArgs.PaymentModel }

    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        isw_pay_code_amount.text = paymentModel.formattedAmount
        isw_pay_code_btn.setOnClickListener {

            // ensure that a paycode has been typed in
            if (payCodeEdt.text.isNullOrEmpty() || payCodeEdt.text.isNullOrBlank()) {
                context?.toast("A Paycode is required")
                return@setOnClickListener
            }
            // ensure that device is connected to internet
            else if (!DeviceUtils.isConnectedToInternet(context!!)) {
                context?.toast("Device is not connected to internet")
                // show no network dialog
                DialogUtils.getNetworkDialog(context!!) {
                    // try submitting again
                    isw_pay_code_btn.performClick()
                }.show()
                return@setOnClickListener
            }

            // disable buttons
            isw_pay_code_btn.isEnabled = false
            isw_pay_code_btn.isClickable = false

            // disable scan button
            isw_qr_code_image.isClickable = false
            isw_qr_code_image.isEnabled = false

            // hide keyboard
            //DisplayUtils.hideKeyboard(activity)

            // start paycode process
            processOnline()

        }

     /*   btnScanCode.setOnClickListener {
            ScanBottomSheet
                .newInstance()
                .show(supportFragmentManager, ScanBottomSheet.toString())
        }*/

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
                    is None -> context?.toast("Unable to process Transaction").also { }//finish() }
                    is Some -> {
                        // set result
                        transactionResult = value
                        // use default transaction because the
                        // transaction is not need for result
                        val txn = Transaction.default()
                        // show transaction result screen
                       // showTransactionResult(txn)
                    }
                }
            }
        }

        // get the paycode
        val code = payCodeEdt.text.toString()
        // initiate paycode purchase
        payCodeViewModel.processOnline(terminalInfo, code, paymentInfo)
    }

    companion object {
        const val TAG = "Pay Code"
    }
}
