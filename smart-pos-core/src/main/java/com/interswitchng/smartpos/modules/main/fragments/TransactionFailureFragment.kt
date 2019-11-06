package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class TransactionFailureFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_transaction_failure

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    companion object {
        const val TAG = "Transaction Failure Fragment"
    }
}
