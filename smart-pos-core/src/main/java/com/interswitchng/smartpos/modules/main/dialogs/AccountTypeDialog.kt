package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import kotlinx.android.synthetic.main.isw_sheet_layout_account_type.*

class AccountTypeDialog constructor(
    private val clickListener: SingleArgsClickListener<Int>
) : BaseBottomSheetDialog() {

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_account_type

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_default_account.setOnClickListener {
            clickListener.invoke(0)
        }
        isw_savings_account.setOnClickListener {
            clickListener.invoke(1)
        }
        isw_current_account.setOnClickListener {
            clickListener.invoke(2)
        }
    }
}