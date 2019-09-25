package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import kotlinx.android.synthetic.main.isw_sheet_layout_payment_options.*

class MakePaymentDialog constructor(
    private val optionClickListener: SingleArgsClickListener<Int>
) : BaseBottomSheetDialog() {

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_payment_options

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_purchase.setOnClickListener {
            optionClickListener.invoke(0)
        }
        isw_pre_authorization.setOnClickListener {
            optionClickListener.invoke(1)
        }
        isw_card_not_present.setOnClickListener {
            optionClickListener.invoke(2)
        }
        isw_completion.setOnClickListener {
            optionClickListener.invoke(3)
        }
    }
}