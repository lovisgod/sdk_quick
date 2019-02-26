package com.interswitchng.smartpos.modules.paycode

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.interswitchng.smartpos.R


internal class ScanBottomSheet : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.isw_content_scan_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            // extract result from scanner
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            val payCodeString = result.contents

            // check result and process payment
            if(payCodeString == null) toast("Cancelled scan")
            else toast(payCodeString)

        }
        else super.onActivityResult(requestCode, resultCode, data)
    }


    companion object {
        fun newInstance() = ScanBottomSheet()
    }
}