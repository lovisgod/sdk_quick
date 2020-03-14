package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import kotlinx.android.synthetic.main.isw_sheet_layout_end_of_day_print.*

class EndOfDayPrintDialog constructor(
        private val clickListener: SingleArgsClickListener<Int>
) : BaseBottomSheetDialog() {

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_end_of_day_print

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_eod_purchase.setOnClickListener {
            clickListener.invoke(0)
        }
        isw_eod_pre_auth.setOnClickListener {
            clickListener.invoke(1)
        }
        isw_eod_completion.setOnClickListener {
            clickListener.invoke(3)
        }
        isw_eod_refund.setOnClickListener {
            clickListener.invoke(4)
        }
        isw_print_all.setOnClickListener {
            clickListener.invoke(5)
        }
    }

    private fun closeAccountDialog() {
        this.dismiss()
    }

    companion object {
        const val TAG = "End of day print Dialog"
    }
}