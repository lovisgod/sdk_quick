package com.interswitchng.smartpos.modules.setup.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_setup_complete.*

class SetupCompleteFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_setup_complete

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isw_finish.setOnClickListener {
            IswPos.showMainActivity()
            requireActivity().finish()
        }
    }

    companion object {
        const val TAG = "Setup Complete Fragment"
    }
}