package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillPaymentCategoriesModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.modules.main.billPayment.utils.PackageBottomSheetDialog
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.pixplicity.easyprefs.library.Prefs
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
            Prefs.putBoolean("isAirtime", true)
            val action = ChooseCategoryFragmentDirections.iswActionIswChoosecategoryfragmentToIswAirtimerechargeinputfragment()
            navigate(action)
        }

        isw_bill_cable_tv.setOnClickListener {
            val action = ChooseCategoryFragmentDirections.iswActionIswChoosecategoryfragmentToIswBillerscategoryfragment()
            Prefs.putString("CATEGORY_CHOSEN", "CABLE")
            findNavController().navigate(action)
        }

        isw_bill_utility.setOnClickListener {
            val action = ChooseCategoryFragmentDirections.iswActionIswChoosecategoryfragmentToIswBillerscategoryfragment()
            Prefs.putString("CATEGORY_CHOSEN", "UTILITY")
            findNavController().navigate(action)
        }


    }


    companion object {
        @JvmStatic
        fun newInstance() = ChooseCategoryFragment()

        const val TAG = "choose category fragment"
    }


}