package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class QrCodeFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_qr_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    companion object {
        const val TAG = "QR Code"
    }
}