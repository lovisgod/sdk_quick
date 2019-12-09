package com.interswitchng.smartpos.modules.bills.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.adapters.BillsAdapter
import com.interswitchng.smartpos.modules.bills.models.BillModel
import com.interswitchng.smartpos.modules.main.MainActivity
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_bills.*

class BillFragment : BaseFragment(TAG), BillsAdapter.BillsItemClickListener {

    private val billFragmentArgs by navArgs<BillFragmentArgs>()
    private val billCategory by lazy { billFragmentArgs.billCategory }

    override fun onBillsItemClicked(item: BillModel) {
        val direction = BillFragmentDirections.iswActionIswBillfragmentToIswBillpaymentfragment22(item)
        navigate(direction)
    }


    override val layoutId: Int
        get() = R.layout.isw_fragment_bills

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeViews()
    }

    private fun initializeViews() {
        isw_activity_customer_name_text.text = billCategory.name

        bills_rv.layoutManager = LinearLayoutManager(context)
        bills_rv.adapter = BillsAdapter(billCategory.bills, context, this)

        backImg.setOnClickListener {
            navigateUp()
        }
    }

    companion object {

        const val TAG = "BILL FRAGMENT"
    }
}