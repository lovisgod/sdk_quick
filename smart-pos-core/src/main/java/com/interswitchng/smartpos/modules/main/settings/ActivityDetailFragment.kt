package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionSuccessModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_activity_detail.*

class ActivityDetailFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_activity_detail

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.isw_activity_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handlePrintReceiptClick()
    }

    private fun handlePrintReceiptClick() {
        isw_print_receipt_label.setOnClickListener {
            val direction = ActivityDetailFragmentDirections.iswActionGotoFragmentReceipt(
                TransactionSuccessModel(amount = 5000)
            )
            navigate(direction)
        }

        isw_activity_nav.setOnClickListener {
            navigateUp()
        }

    }

    companion object {
        const val TAG = "ACTIVITY DETAIL FRAGMENT"
    }
}
