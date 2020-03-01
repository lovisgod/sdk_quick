package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import kotlinx.android.synthetic.main.isw_sheet_layout_payment_type_options.*

class PaymentTypeDialog constructor(
    private val currentlySelected: PaymentModel.PaymentType? = null,
    private val paymentTypeClickListener: SingleArgsClickListener<PaymentModel.PaymentType>
): BaseBottomSheetDialog() {

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_payment_type_options

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentlySelected?.let {
            when (it) {
                PaymentModel.PaymentType.CARD -> isw_card_payment.setBackgroundResource(R.drawable.isw_selected_background)
                PaymentModel.PaymentType.QR_CODE -> isw_qr_code.setBackgroundResource(R.drawable.isw_selected_background)
                PaymentModel.PaymentType.PAY_CODE -> isw_pay_code.setBackgroundResource(R.drawable.isw_selected_background)
                else -> isw_ussd.setBackgroundResource(R.drawable.isw_selected_background)
            }
        }
        isw_card_payment.setOnClickListener {
            paymentTypeClickListener.invoke(PaymentModel.PaymentType.CARD)
            dismiss()
        }
        isw_qr_code.setOnClickListener {
            paymentTypeClickListener.invoke(PaymentModel.PaymentType.QR_CODE)
            dismiss()
        }
        isw_pay_code.setOnClickListener {
            paymentTypeClickListener.invoke(PaymentModel.PaymentType.PAY_CODE)
            dismiss()
        }
        isw_ussd.setOnClickListener {
            paymentTypeClickListener.invoke(PaymentModel.PaymentType.USSD)
            dismiss()
        }
        isw_card_not_present.setOnClickListener{
            paymentTypeClickListener.invoke(PaymentModel.PaymentType.CARD_NOT_PRESENT)
            dismiss()
        }
    }
}