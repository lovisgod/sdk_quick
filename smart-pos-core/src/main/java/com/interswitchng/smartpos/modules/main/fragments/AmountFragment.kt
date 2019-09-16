package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.databinding.IswFragmentAmountBinding
import com.interswitchng.smartpos.shared.activities.BaseFragment

class AmountFragment : BaseFragment<IswFragmentAmountBinding>(TAG) {

    override fun getLayoutId(): Int = R.layout.isw_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    companion object {
        const val TAG = "AMOUNT FRAGMENT"
    }
}