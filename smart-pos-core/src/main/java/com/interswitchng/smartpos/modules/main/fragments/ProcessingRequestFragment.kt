package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_processing_transaction.*

class ProcessingRequestFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_processing_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Handler().postDelayed({
            isw_connecting.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.isw_round_done, 0,0,0)
                text = "Connected"

                isw_connection_progress.progress = isw_connection_progress.progress + 30
            }
        }, 2000)
    }

    companion object {
        const val TAG = "Processing Request Fragment"
    }
}