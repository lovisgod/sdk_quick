package com.interswitchng.interswitchpossdk.shared.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.modules.card.CardActivity
import com.interswitchng.interswitchpossdk.modules.paycode.PayCodeActivity
import com.interswitchng.interswitchpossdk.modules.ussdqr.QrCodeActivity
import com.interswitchng.interswitchpossdk.modules.ussdqr.UssdActivity
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
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
        val context = ussdPayment.context

        ussdPayment.setOnClickListener {
            val ussdIntent = Intent(context, UssdActivity::class.java)
            ussdIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            ussdIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(ussdIntent)
        }

        qrPayment.setOnClickListener {
            val qrIntent = Intent(context, QrCodeActivity::class.java)
            qrIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            qrIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(qrIntent)
        }

        cardPayment.setOnClickListener {
            val cardIntent = Intent(context, CardActivity::class.java)
            cardIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            cardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(cardIntent)
        }

        payCodePayment.setOnClickListener {
            val payCodeIntent = Intent(context, PayCodeActivity::class.java)
            payCodeIntent.putExtra(Constants.KEY_PAYMENT_INFO, info)
            payCodeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

        fun newInstance(excludedOption: String = NONE, info: PaymentInfo) = BottomSheetOptionsDialog().apply {
            arguments = Bundle().apply {
                putString(KEY_EXCLUDED_OPTION, excludedOption)
                putParcelable(Constants.KEY_PAYMENT_INFO, info)
            }
        }
    }
}