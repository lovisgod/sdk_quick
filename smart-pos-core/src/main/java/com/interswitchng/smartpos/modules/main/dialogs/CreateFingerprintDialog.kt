package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.viewmodels.FingerprintViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.isw_sheet_layout_register_fingerprint.*
import org.koin.android.viewmodel.ext.android.viewModel

class CreateFingerprintDialog : BaseBottomSheetDialog() {

    private val viewModel: FingerprintViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_register_fingerprint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
    }



    companion object {
        const val TAG = "Create Fingerprint Dialog"
    }
}