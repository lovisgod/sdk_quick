package com.interswitchng.smartpos.modules.main.billPayment.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.interswitchng.smartpos.R

class RechargeSummaryDialog(): DialogFragment() {
    val TAG = "isw_recharge_summary_dialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NO_FRAME, theme);
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        var rootView: View = inflater.inflate(R.layout.isw_recharge_summary_dialog, container, false)
        return rootView
    }
}