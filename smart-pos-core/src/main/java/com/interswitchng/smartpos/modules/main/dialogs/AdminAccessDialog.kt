package com.interswitchng.smartpos.modules.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.utilities.NoArgsClickListener
import kotlinx.android.synthetic.main.isw_sheet_layout_admin_access.*

class AdminAccessDialog constructor(
    private val authenticationListener: NoArgsClickListener
) : BaseBottomSheetDialog() {

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_admin_access

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        object : CountDownTimer(4000, 1000) {
            override fun onFinish() {
                authenticationListener.invoke()
                dismiss()
            }

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 2000L) {
                    isw_admin_access.visibility = View.GONE
                    isw_admin_access_granted.visibility = View.VISIBLE
                }
            }
        }.start()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val sheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

    companion object {
        const val TAG = "Admin Access Dialog"
    }
}