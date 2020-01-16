package com.interswitchng.smartpos.modules.main.fragments


import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.ussdqr.adapters.BankListAdapter
import com.interswitchng.smartpos.modules.ussdqr.viewModels.UssdViewModel
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.*
import kotlinx.android.synthetic.main.isw_activity_ussd.*
import kotlinx.android.synthetic.main.isw_fragment_ussd.*
import kotlinx.android.synthetic.main.isw_generating_code_layout.*
import kotlinx.android.synthetic.main.isw_select_bank_view.*
import kotlinx.android.synthetic.main.isw_ussd_content_layout.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 *
 */

const val GENERATING_USSD_VIEW = 1
const val USSD_CODE_DETAIL_VIEW = 2

class UssdFragment : BaseFragment(TAG) {

    private var selectedBankName = EMPTY_STRING

    private val ussdViewModel: UssdViewModel by viewModel()

    private lateinit var paymentTypeDialog: PaymentTypeDialog

    private var ussdCode: String? = null
    private val dialog by lazy { DialogUtils.getLoadingDialog(context!!) }
    private val alert by lazy { DialogUtils.getAlertDialog(context!!) }
   // private val logger by lazy { Logger.with("USSD") }

    //private lateinit var banksDialog: SelectBankBottomSheet

    private val adapter: BankListAdapter = BankListAdapter {
        if (ussdProceed.isNotVisible()) ussdProceed.show()
    }

    private val _selectedBank = MutableLiveData<Bank>()
    val selectedBank: LiveData<Bank> get() = _selectedBank

    var hasBanks = false

    private val ussdFragmentArgs by navArgs<UssdFragmentArgs>()
    private val paymentModel by lazy { ussdFragmentArgs.PaymentModel }

    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan())
    }

    override val layoutId: Int
        get() = R.layout.isw_fragment_ussd


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }
    private fun setupUI() {
        // Set up Bank Selection
        setUpBankSelectionUI()
        showPaymentOptions()
        if (DeviceUtils.isConnectedToInternet(context!!)) {
            // load banks if not loaded
            if (hasBanks.not()) runWithInternet {
                ussdViewModel.loadBanks()
            }
            // observe viewModel
            observeViewModel()
        } else runWithInternet {
            setupUI()
        }
    }

    private fun showPaymentOptions() {
        change_payment_method.setOnClickListener {
            paymentTypeDialog = PaymentTypeDialog(PaymentModel.PaymentType.CARD) {
                when (it) {
                    PaymentModel.PaymentType.USSD -> {}
                    PaymentModel.PaymentType.PAY_CODE -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentPayCode(paymentModel)
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

    private fun setUpBankSelectionUI() {
        rvBankLists.layoutManager = GridLayoutManager(activity, 3)
        rvBankLists.adapter = adapter
        //closeSheetButton.setOnClickListener { dismiss() }

        // set visible if adpater has no banks
        progressBarSelectBank.visibility =
            if (adapter.itemCount > 0) View.GONE
            else View.VISIBLE

        // set click listener for adapter's selected bank§
        ussdProceed.setOnClickListener {
            adapter.selectedBank?.also {
                // set the selected bank
                _selectedBank.value = it
                switchToGeneratingUssdView()
            } ?: context?.toast("Please select a Bank") // else prompt user to select bank
        }

    }

    fun loadBanks(banks: List<Bank>) {
        hasBanks = banks.isNotEmpty()
        adapter.setBanks(banks)
        progressBarSelectBank.visibility = View.GONE
    }

    private fun switchToGeneratingUssdView() {
        ussdAnimator.displayedChild = GENERATING_USSD_VIEW
        isw_generating_code_text?.text = "Generating Ussd code"
        isw_progressbar.progressTintList = ColorStateList.valueOf(Color.WHITE)
    }


    private fun observeViewModel() {
        // func to return lifecycle
        val owner = { lifecycle }

        // observe view model
        with(ussdViewModel) {

            // observe print button
           /* printButton.observe(owner) {
                val isEnabled = it ?: false
                printCodeButton.isEnabled = isEnabled
                printCodeButton.isClickable = isEnabled
            }*/

            // observe bank list
            allBanks.observe(owner) {
                it?.let { banks ->
                    when (banks) {
                        is Some -> loadBanks(banks.value)
                        is None -> context?.toast("Unable to load bank list")
                    }
                }
            }

            // observe selected bank code
            bankCode.observe(owner) {
                // dismiss loading dialog
                dialog.dismiss()

                // handle code response
                it?.let { code ->
                    when (code) {
                        is Some -> handleResponse(code.value)
                        is None -> handleError()
                    }
                }
            }

            // observe payment status
            paymentStatus.observe(owner) {
                it?.let { status ->

                    // setup payment slip
                    if (it is PaymentStatus.Timeout) {
                        val transaction = Transaction(
                            -1, paymentInfo.amount,
                            "",
                            "0X0X",
                            terminalInfo.currencyCode,
                            true,
                            null,
                            0,
                            "Pending")

                        //val result = getTransactionResult(transaction)
                        //printSlip = result?.getSlip(terminalInfo)?.getSlipItems() ?: printSlip
                    }

                    handlePaymentStatus(status)
                }
            }

            // observe progress bar
            showProgress.observe(owner) {
                if (it != true) return@observe

                // show progress alert
                showProgressAlert {
                    ussdViewModel.cancelPoll()
                    onCheckStopped()
                }
            }
        }

        // observe bank selection
        selectedBank.observe(owner) {
            it?.apply(::getBankCode)
        }

    }

    private fun getBankCode(selectedBank: Bank) {
        if (DeviceUtils.isConnectedToInternet(context!!)) {

            selectedBankName = selectedBank.name
            // show loading dialog
            dialog.show()

            // create payment info with bank code
            val info = PaymentInfo(paymentInfo.amount, selectedBank.code)
            val request = CodeRequest.from(iswPos.config.alias, terminalInfo, info,
                CodeRequest.TRANSACTION_USSD
            )

            // get ussd code
            ussdViewModel.getBankCode(request)
        } else runWithInternet {
            // re-trigger get bank code
            getBankCode(selectedBank)
        }
    }

    private fun showButtons(response: CodeResponse) {
        initiateButton.isEnabled = true
        initiateButton.setOnClickListener {
            if (DeviceUtils.isConnectedToInternet(context!!)) {
                initiateButton.isEnabled = false
                initiateButton.isClickable = false

                val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
                // check transaction status
                ussdViewModel.checkTransactionStatus(status)

            } else runWithInternet {
                // re-perform click
                initiateButton.performClick()
            }
        }

        printCodeButton.isEnabled = true
        printCodeButton.setOnClickListener {
            //ussdViewModel.printCode(this, posDevice, UserType.Customer, printSlip)
        }
    }


    private fun handleResponse(response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {

                updateProgressBarAndSwitchView()

                // ensure internet connection before polling for transaction
                if (DeviceUtils.isConnectedToInternet(context!!)) {

                    ussdCode = response.bankShortCode ?: response.defaultShortCode
                    ussdCode?.apply {
                        //ussdCodeText.text = this
                        val code = substring(lastIndexOf("*") + 1 until lastIndexOf("#"))

                        // get the entire hint as spannable string
                        val hint = getString(R.string.isw_ussd_code_hint_text, selectedBankName, this)
                        val spannableHint = SpannableString(hint)

                        // increase font size and change font color
                        val startIndex = selectedBankName.length
                        val endIndex = startIndex + this.length + 3
                        val codeColor = ContextCompat.getColor(context!!, R.color.iswTransparent)
                        spannableHint.setSpan(AbsoluteSizeSpan(24, true), startIndex, endIndex, 0)
                        spannableHint.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
                        spannableHint.setSpan(ForegroundColorSpan(codeColor), startIndex, endIndex, 0)

                        ussdCodeText.text = spannableHint

                        ussdQuickTellerCode.text = code

                        isw_ussd_amount.text = paymentModel.formattedAmount


                        // set the text value
                        //paymentHint.text = spannableHint
                        //printSlip.add(PrintObject.Data("code - \n $this\n", PrintStringConfiguration(isBold = true, isTitle = true)))
                    }

                    // show buttons
                   // showButtons(response)

                    // check transaction status
                    val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
                    ussdViewModel.pollTransactionStatus(status)

                } else runWithInternet {
                    // re-trigger handle response
                    handleResponse(response)
                }
            }
            else -> {
                activity?.runOnUiThread {
                    val errorMessage = "An error occured: ${response.responseDescription}"
                    context?.toast(errorMessage)
                    handleError()
                }
            }
        }
    }

    private fun updateProgressBarAndSwitchView() {
        isw_progressbar.progress = 90
        ussdAnimator.displayedChild = USSD_CODE_DETAIL_VIEW
    }

    private fun handleError() {
        val selectedBank = selectedBank.value!!
        alert.setPositiveButton(R.string.isw_title_try_again) { dialog, _ -> dialog.dismiss(); getBankCode(selectedBank) }
            .setNegativeButton(R.string.isw_title_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /*override fun getTransactionResult(transaction: Transaction): TransactionResult? {
        val now = Date()

        val responseMsg = IsoUtils.getIsoResult(transaction.responseCode)?.second
            ?: transaction.responseDescription
            ?: "Error"

        return TransactionResult(
            paymentType = PaymentType.USSD,
            dateTime = DisplayUtils.getIsoString(now),
            amount = DisplayUtils.getAmountString(paymentInfo),
            type = TransactionType.Purchase,
            authorizationCode = transaction.responseCode,
            responseMessage = responseMsg,
            responseCode = transaction.responseCode,
            cardPan = "", cardExpiry = "", cardType = CardType.None,
            stan = paymentInfo.getStan(), pinStatus = "", AID = "", code = ussdCode!!,
            telephone = iswPos.config.merchantTelephone
        )
    }*/
/*
    override fun onCheckStopped() {
        super.onCheckStopped()
        initiateButton.isEnabled = true
        initiateButton.isClickable = true
        ussdViewModel.cancelPoll()
    }*/


    companion object {
        const val TAG = "Ussd Fragment"
    }


}
