package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.viewmodels.FingerprintViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.models.fingerprint.FingerprintResult
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
        isw_capture_fingerprint.setOnClickListener {
            fingerprintViewModel.createFingerprint(sheetContext)
        }
    }

    private fun handleFingerprintResult(result: FingerprintResult) {
        when (result) {
            FingerprintResult.Success -> {
                isw_textview13.apply {
                    text = resources.getString(R.string.isw_fingerprint_recognised)
                    setTextColor(ContextCompat.getColor(sheetContext, R.color.iswTextColorSuccessDark))
                }
                Handler().postDelayed({
                    responseListener.invoke(true)
                    dismiss()
                }, 1000)
            }
        }
    }

    companion object {
        const val TAG = "Fingerprint Dialog"
    }
}