package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class ActivityDetailReceiptFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_activity_detail_receipt

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    companion object {
        const val TAG = "Activity Detail Receipt Fragment"
    }


}
