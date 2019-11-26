package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.viewmodels.FingerprintViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.models.fingerprint.Fingerprint
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import kotlinx.android.synthetic.main.isw_sheet_layout_admin_fingerprint.*
import org.koin.android.viewmodel.ext.android.viewModel

class FingerprintBottomDialog constructor(
    private val responseListener: SingleArgsClickListener<Boolean>
) : BaseBottomSheetDialog() {

    private val fingerprintViewModel by viewModel<FingerprintViewModel>()

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_admin_fingerprint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fingerprintViewModel.fingerPrintResult.observe(this, Observer {
            it?.let(::handleFingerprintResult)
        })
        //1AC10B
        fingerprintViewModel.createFingerprint(sheetContext)
    }

    private fun handleFingerprintResult(result: Fingerprint) {
        when (result) {
            is Fingerprint.Success -> {
                dismiss()
                responseListener.invoke(true)
            }
            is Fingerprint.Detected -> {
                isw_textview13.apply {
                    text = resources.getString(R.string.isw_fingerprint_recognised)
                    setTextColor(ContextCompat.getColor(sheetContext, R.color.iswTextColorSuccessDark))
                }
                isw_admin_fingerprint.setImageResource(R.drawable.ic_fingerprint_detected)
            }
            is Fingerprint.Failed -> {
                responseListener.invoke(false)
            }
        }
    }

    companion object {
        const val TAG = "Fingerprint Dialog"
    }
}