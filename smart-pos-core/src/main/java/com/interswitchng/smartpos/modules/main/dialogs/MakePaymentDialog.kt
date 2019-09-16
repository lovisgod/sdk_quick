package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.databinding.IswLayoutPaymentOptionsSheetBinding
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener

class MakePaymentDialog constructor(
    private val optionClickListener: SingleArgsClickListener<Int>
) : BaseBottomSheetDialog <IswLayoutPaymentOptionsSheetBinding>() {

    override fun getLayoutId(): Int = R.layout.isw_layout_payment_options_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.iswPurchase.setOnClickListener {
            optionClickListener.invoke(0)
        }
        binding.iswPreAuthorization.setOnClickListener {
            optionClickListener.invoke(1)
        }
        binding.iswCardNotPresent.setOnClickListener {
            optionClickListener.invoke(2)
        }
        binding.iswCompletion.setOnClickListener {
            optionClickListener.invoke(3)
        }
    }
}