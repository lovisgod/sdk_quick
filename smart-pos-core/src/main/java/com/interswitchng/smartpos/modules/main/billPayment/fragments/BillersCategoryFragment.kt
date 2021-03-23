package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.interswitchng.smartpos.R


class BillersCategoryFragment : DialogFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false
        super.onCreateView(inflater, container, savedInstanceState)
        var rootView: View = inflater.inflate(R.layout.isw_fragment_billers_category, container, false)
        return rootView
    }

    private fun setUpView() {

    }

    companion object {
        @JvmStatic
        fun newInstance() = BillersCategoryFragment()
        val TAG = "BillerCategoryFragment"
    }


}