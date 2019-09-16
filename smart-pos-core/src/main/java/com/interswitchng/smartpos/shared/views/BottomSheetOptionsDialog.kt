package com.interswitchng.smartpos.shared.views

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardActivity
import com.interswitchng.smartpos.modules.home.HomeActivity
import com.interswitchng.smartpos.modules.paycode.PayCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.UssdActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import kotlinx.android.synthetic.main.isw_content_payment_options.*


/**
 * This class is the inflatable bottom sheet that presents
 * the SDK's supported payment options
 */
class BottomSheetOptionsDialog : BottomSheetDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val sheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

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
        val startActivity = { intent: Intent ->
            val isIswActivity = when (context) {
                is BaseActivity,
                is HomeActivity -> true
                else -> false
            }

            intent.putExtra(Constants.KEY_PAYMENT_INFO, info)

            if (isIswActivity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                context.startActivity(intent)
            } else {
                context.startActivityForResult(intent, IswPos.CODE_PURCHASE)
            }
        }

        ussdPayment.setOnClickListener {
            val ussdIntent = Intent(context, UssdActivity::class.java)
            startActivity(ussdIntent)
            dismiss()
        }

        qrPayment.setOnClickListener {
            val qrIntent = Intent(context, QrCodeActivity::class.java)
            startActivity(qrIntent)
            dismiss()
        }

        cardPayment.setOnClickListener {
            val cardIntent = Intent(context, CardActivity::class.java)
            startActivity(cardIntent)
            dismiss()
        }

        payCodePayment.setOnClickListener {
            val payCodeIntent = Intent(context, PayCodeActivity::class.java)
            startActivity(payCodeIntent)
            dismiss()
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