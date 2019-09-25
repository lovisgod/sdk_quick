package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_pin.*

class PinFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_pin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_enter.setOnClickListener {

        }
    }

    companion object {
        const val TAG = "Pin Fragment"
    }
}