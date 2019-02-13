package com.interswitchng.interswitchpossdk.shared.views

import android.content.Intent
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
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.content_payment_options.*

class BottomSheetOptionsDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_bottom_sheet_options, container, false)
    }

    override fun onStart() {
        super.onStart()

        arguments?.apply {
            val view = when (getString(KEY_EXCLUDED_OPTION)) {
                USSD -> ussdPayment
                CARD -> cardPayment
                PAYCODE -> payCodePayment
                else -> qrPayment
            }

            view.visibility = View.GONE

            DisplayUtils.setupPaymentOptions(this@BottomSheetOptionsDialog.view!!, getParcelable(Constants.KEY_PAYMENT_INFO)!!)

        } ?: dismiss() // dismiss if no arguments were passed
    }

    companion object {
        private const val KEY_EXCLUDED_OPTION = "excluded_option"
        const val USSD = "ussd"
        const val QR = "qr"
        const val PAYCODE = "pay_code"
        const val CARD = "card"
        fun newInstance(excludedOption: String, info: PaymentInfo) = BottomSheetOptionsDialog().apply {
            arguments = Bundle().apply {
                putString(KEY_EXCLUDED_OPTION, excludedOption)
                putParcelable(Constants.KEY_PAYMENT_INFO, info)
            }
        }
    }
}