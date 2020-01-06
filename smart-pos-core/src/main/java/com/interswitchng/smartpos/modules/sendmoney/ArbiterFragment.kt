package com.interswitchng.smartpos.modules.sendmoney


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_arbiter.*
import kotlinx.android.synthetic.main.isw_fragment_arbiter.arbiterSpinner
import kotlinx.android.synthetic.main.isw_fragment_arbiter.backImg
import kotlinx.android.synthetic.main.isw_fragment_arbitration_status.*
import kotlinx.android.synthetic.main.isw_fragment_transaction.*
import kotlinx.android.synthetic.main.isw_item_arbiter_status_list.*


class ArbiterFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_arbiter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {
        backImg.setOnClickListener {
            navigateUp()
        }

        val plans = resources.getStringArray(R.array.isw_dummy_dispute_types)
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            plans
        )
        arbiterSpinner.adapter = adapter
        arbiterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }

            }

        isw_check_status.setOnClickListener(){
            val direction = ArbiterFragmentDirections.iswActionGlobalIswArbitrationstatusfragment()
            navigate(direction)
        }

        isw_raise.setOnClickListener(){
            Toast.makeText(context, "Dispute Raised", Toast.LENGTH_LONG).show()
            isw_amount.text = null
            isw_amount.text = null
            isw_reference_date.text = null
        }
    }

    companion object {
        const val TAG = "Arbitration FRAGMENT"
    }
}
