package com.interswitchng.smartpos.modules.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.NoArgsClickListener

class ContactlessDialog constructor(
    private val authenticationListener: NoArgsClickListener
) : BaseBottomSheetDialog() {

    override val layoutId: Int
        get() = R.layout.isw_card_not_present


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            //val sheetDialog = it as BottomSheetDialog
            //val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
           // BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

    companion object {
        const val TAG = "Contactless Dialog"
    }
}