package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class ChooseCategoryFragment : BaseFragment(TAG) {
    override val layoutId: Int
        get() = R.layout.isw_fragment_choose_category


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance() = ChooseCategoryFragment()

        const val TAG = "choose category fragment"
    }
}