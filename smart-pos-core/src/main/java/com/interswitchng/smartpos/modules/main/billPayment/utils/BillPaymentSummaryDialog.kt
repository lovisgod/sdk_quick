package com.interswitchng.smartpos.modules.main.billPayment.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillSummaryModel
import kotlinx.android.synthetic.main.isw_recharge_summary_dialog.*

class BillPaymentSummaryDialog(private val details: BillSummaryModel?): DialogFragment() {
    val TAG = "isw_recharge_summary_dialog"
    lateinit var baseView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        var rootView: View = inflater.inflate(R.layout.isw_recharge_summary_dialog, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun setupUI() {
        if(details !== null){

        }
    }

    fun attachSummaryText() {
        isw_recharge_summary_description.text = details?.summaryText
    }


}