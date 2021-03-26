package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_choose_category.*

class ChooseCategoryFragment : BaseFragment(TAG) {
    override val layoutId: Int
        get() = R.layout.isw_fragment_choose_category


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeClicks()
    }

    private fun observeClicks() {
        isw_bill_airtime.setOnClickListener {
            val action = ChooseCategoryFragmentDirections.iswActionIswChoosecategoryfragmentToIswAirtimerechargeinputfragment()
            navigate(action)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChooseCategoryFragment()

        const val TAG = "choose category fragment"
    }


}