package com.interswitchng.smartpos.modules.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_receipt.*

class ReceiptFragment : BaseFragment(TAG) {

    private val receiptFragmentArgs by navArgs<ReceiptFragmentArgs>()
    private val paymentModel by lazy { receiptFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_receipt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (paymentModel.type) {
            PaymentModel.MakePayment.PURCHASE -> isw_receipt_text.text = getString(R.string.isw_thank_you)
            PaymentModel.MakePayment.PRE_AUTHORIZATION -> isw_receipt_text.text = getString(R.string.isw_pre_authorization_completed)
            PaymentModel.MakePayment.CARD_NOT_PRESENT -> isw_receipt_text.text = getString(R.string.isw_card_not_present_completed)
        }

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
    }

    companion object {
        const val TAG = "Receipt Fragment"
    }
}