package com.interswitchng.smartpos.modules.main.billPayment.fragments


import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment


class ChoosePackageFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_choose_package

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        @JvmStatic
        fun newInstance() = ChoosePackageFragment()
        val TAG = "CHOOSE PACKAGE FRAGMENT"
    }


}