package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_processing_transaction.*

class ProcessingRequestFragment : BaseFragment(TAG) {

    private val processingRequestFragmentArgs by navArgs<ProcessingRequestFragmentArgs>()
    private val payment by lazy { processingRequestFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_processing_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (payment.type) {
            PaymentModel.MakePayment.PURCHASE -> isw_processing_text.text = getString(R.string.isw_processing_transaction, "Transaction")
            PaymentModel.MakePayment.PRE_AUTHORIZATION -> isw_processing_text.text = getString(R.string.isw_processing_transaction, "Pre-Authorization")
            PaymentModel.MakePayment.CARD_NOT_PRESENT -> isw_processing_text.text = getString(R.string.isw_processing_transaction, "Card-Not-Present")
        }
        isw_connecting.setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_gray_stroke, 0, 0, 0)
        isw_authenticating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_gray_stroke, 0, 0, 0)
        isw_receiving.setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_gray_stroke, 0, 0, 0)
        Handler().postDelayed({
            isw_connecting.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_done, 0,0,0)
                text = "Connected"

                isw_connection_progress.progress = isw_connection_progress.progress + 30
            }
        }, 2000)
        Handler().postDelayed({
            isw_authenticating.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_done, 0,0,0)
                text = "Authenticated"

                isw_connection_progress.progress = isw_connection_progress.progress + 30
            }
        }, 4000)
        Handler().postDelayed({
            isw_receiving.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_done, 0,0,0)
                text = "Received"

                isw_connection_progress.progress = isw_connection_progress.progress + 30
            }
        }, 6000)

        Handler().postDelayed({
            val direction = ProcessingRequestFragmentDirections.iswActionGotoFragmentReceipt(payment)
            navigate(direction)
        }, 7000)
    }

    companion object {
        const val TAG = "Processing Request Fragment"
    }
}