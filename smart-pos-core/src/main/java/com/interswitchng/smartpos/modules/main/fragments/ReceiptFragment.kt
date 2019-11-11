package com.interswitchng.smartpos.modules.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import kotlinx.android.synthetic.main.isw_fragment_receipt.*

class ReceiptFragment : BaseFragment(TAG) {

    private val receiptFragmentArgs by navArgs<ReceiptFragmentArgs>()
    private val transactionResponseModel by lazy { receiptFragmentArgs.TransactionResponseModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_receipt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpUI()
    }

    private fun displayTransactionResultIconAndMessage() {
        when (transactionResponseModel.transactionResult?.responseCode) {
            IsoUtils.TIMEOUT_CODE -> {
                transactionResponseIcon.setImageResource(R.drawable.isw_failure)
                isw_receipt_text.text = "Failed!"
                isw_transaction_msg.text = "Your transaction was unsuccessful"
            }

            IsoUtils.OK -> {
                transactionResponseIcon.setImageResource(R.drawable.isw_round_done_padded)
                isw_transaction_msg.text = "Your transaction was successful"
                when (transactionResponseModel.transactionType) {
                    PaymentModel.TransactionType.CARD_PURCHASE -> isw_receipt_text.text = getString(R.string.isw_thank_you)
                    PaymentModel.TransactionType.PRE_AUTHORIZATION -> isw_receipt_text.text = getString(R.string.isw_pre_authorization_completed)
                    PaymentModel.TransactionType.CARD_NOT_PRESENT -> isw_receipt_text.text = getString(R.string.isw_card_not_present_completed)
                }
            }
        }
    }

    private fun handleClicks() {

        isw_done.setOnClickListener {
            navigateUp()
        }

        isw_share_receipt.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, "")
                type = "image/*"
            }
            startActivity(Intent.createChooser(shareIntent, "Select Application"))
        }

        isw_done.setOnClickListener {
            val direction = ReceiptFragmentDirections.iswActionGotoFragmentTransaction()
            navigate(direction)
        }

    }

    private fun setUpUI() {
        displayTransactionResultIconAndMessage()
        handleClicks()
    }

    companion object {
        const val TAG = "Receipt Fragment"
    }
}