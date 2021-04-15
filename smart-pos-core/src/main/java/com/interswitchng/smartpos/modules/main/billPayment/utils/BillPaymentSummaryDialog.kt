package com.interswitchng.smartpos.modules.main.billPayment.utils

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillSummaryModel
import com.interswitchng.smartpos.modules.main.billPayment.models.DialogCallBackListener
import kotlinx.android.synthetic.main.isw_recharge_summary_dialog.*

class BillPaymentSummaryDialog(private val details: BillSummaryModel?, private val callBackListener: DialogCallBackListener<Boolean>): DialogFragment() {
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
        setupUI()
        observeClicks()
    }

    fun setupUI() {
        if(details !== null){
            attachImage()
            attachSummaryText()
            setupSummaryBody()
        }
    }

    fun attachImage() {
        if (details?.image !== null){
           val imageContainer = isw_recharge_summary_logo
            imageContainer.visibility = View.VISIBLE
            imageContainer.setImageResource(details.image)
        }
    }

    fun setupSummaryBody() {
        val body = isw_summary_detail_body
        if (details != null) {
            for (detail in details.details) {
                val header = TextView(ContextThemeWrapper(context, R.style.isw_summary_details_header))
                val summaryDetails = TextView(ContextThemeWrapper(context, R.style.isw_summary_details))
                summaryDetails.text = detail.subtitle
                header.text = detail.title
                body.addView(header)
                val idh: Int = isw_summary_detail_body.indexOfChild(header)
                body.addView(summaryDetails)
                body.tag = idh.toString()
                val ids: Int = body.indexOfChild(summaryDetails)
                body.tag = ids.toString()
            }

        }

    }

    fun attachSummaryText() {
        isw_recharge_summary_description.text = details?.summaryText
    }

    fun observeClicks(){
        isw_recharge_summary_cancel.setOnClickListener {
            this.dismiss()
        }

        isw_recharge_summary_continue.setOnClickListener {
            this.dismiss()
            callBackListener.onDialogDataReceived(true)
        }
    }


}