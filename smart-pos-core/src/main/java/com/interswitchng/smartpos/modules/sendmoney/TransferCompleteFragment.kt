package com.interswitchng.smartpos.modules.sendmoney


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment


class TransferCompleteFragment : BaseFragment(TAG) {


    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_complete

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {

    }

    companion object {

        const val TAG = "TRANSFER COMPLETE FRAGMENT"
    }


}
