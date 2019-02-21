package com.interswitchng.smartpos.shared.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardActivity
import com.interswitchng.smartpos.modules.paycode.PayCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.UssdActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import kotlinx.android.synthetic.main.isw_content_payment_options.*

class BottomSheetOptionsDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.isw_content_bottom_sheet_options, container, false)
    }

    override fun onStart() {
        super.onStart()

        arguments?.apply {
            val view = when (getString(KEY_EXCLUDED_OPTION)) {
                USSD -> ussdPayment
                CARD -> cardPayment
                PAYCODE -> payCodePayment
                QR -> qrPayment
                else -> null
            }

            view?.visibility = View.GONE

            setupUI(getParcelable(Constants.KEY_PAYMENT_INFO)!!)

        } ?: dismiss() // dismiss if no arguments were passed
    }

    private fun setupUI(info: PaymentInfo) {
        val context = requireActivity()
        val flag = Intent.FLAG_ACTIVITY_NEW_TASK

        ussdPayment.setOnClickListener {
            val ussdIntent = Intent(context, UssdActivity::class.java)
            ussdIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            context.startActivity(ussdIntent)
        }

        qrPayment.setOnClickListener {
            val qrIntent = Intent(context, QrCodeActivity::class.java)
            qrIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            context.startActivity(qrIntent)
        }

        cardPayment.setOnClickListener {
            val cardIntent = Intent(context, CardActivity::class.java)
            cardIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            context.startActivity(cardIntent)
        }

        payCodePayment.setOnClickListener {
            val payCodeIntent = Intent(context, PayCodeActivity::class.java)
            payCodeIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            context.startActivity(payCodeIntent)
        }

        closeBtn.setOnClickListener { dismiss() }

        // set outline provider for lollipop or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val outlineProvider = TweakableOutlineProvider()
            ussdPayment.outlineProvider = outlineProvider
            qrPayment.outlineProvider = outlineProvider
            cardPayment.outlineProvider = outlineProvider
            payCodePayment.outlineProvider = outlineProvider
        }
    }

    companion object {
        private const val KEY_EXCLUDED_OPTION = "excluded_option"
        const val USSD = "ussd"
        const val QR = "qr"
        const val PAYCODE = "pay_code"
        const val CARD = "card"
        const val NONE = "none"

        internal fun newInstance(excludedOption: String = NONE, info: PaymentInfo) = BottomSheetOptionsDialog().apply {
            arguments = Bundle().apply {
                putString(KEY_EXCLUDED_OPTION, excludedOption)
                putParcelable(Constants.KEY_PAYMENT_INFO, info)
            }
        }
    }
}