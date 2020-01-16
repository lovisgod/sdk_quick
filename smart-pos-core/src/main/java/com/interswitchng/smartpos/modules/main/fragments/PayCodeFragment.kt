package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.paycode.PayCodeViewModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_paycode.*
import org.koin.android.viewmodel.ext.android.viewModel

class PayCodeFragment : BaseFragment(TAG) {
    private val payCodeViewModel: PayCodeViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_fragment_paycode

    private val payCodeFragmentArgs by navArgs<PayCodeFragmentArgs>()
    private val paymentModel by lazy { payCodeFragmentArgs.PaymentModel }

    private lateinit var paymentTypeDialog: PaymentTypeDialog

    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        showPaymentOptions()
        isw_pay_code_amount.text = paymentModel.formattedAmount
        isw_card_details_toolbar.setNavigationOnClickListener { navigateUp() }

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

    private fun showPaymentOptions() {
        change_payment_method.setOnClickListener {
            paymentTypeDialog = PaymentTypeDialog(PaymentModel.PaymentType.CARD) {
                when (it) {
                    PaymentModel.PaymentType.PAY_CODE -> {}
                    PaymentModel.PaymentType.USSD -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentUssd(paymentModel)
                        navigate(direction)
                    }
                    PaymentModel.PaymentType.QR_CODE -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentQrCodeFragment(paymentModel)
                        navigate(direction)
                    }
                    PaymentModel.PaymentType.CARD -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentCardTransactions(paymentModel)
                        navigate(direction)
                    }
                }

            }
            paymentTypeDialog.show(childFragmentManager, CardTransactionsFragment.TAG)
        }
    }

    private fun processOnline() {

        // change hint text
        //paymentHint.text = getString(R.string.isw_title_processing_transaction)
        // show transaction progress alert
        showProgressAlert(false)

        // observe result
        payCodeViewModel.transactionResult.observe({lifecycle}) {
            it?.apply {
                when (this) {
                    is None -> context?.toast("Unable to process Transaction").also { }//finish() }
                    is Some -> {
                        // set result
                        //transactionResult = value
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
