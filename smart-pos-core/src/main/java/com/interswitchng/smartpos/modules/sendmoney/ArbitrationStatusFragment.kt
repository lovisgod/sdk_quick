package com.interswitchng.smartpos.modules.sendmoney

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_arbitration_status.*


class ArbitrationStatusFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_arbitration_status

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

        isw_proceed.setOnClickListener {

            val searchString = isw_reference_phonenumber.text;
            if(!TextUtils.isEmpty(searchString)) {
                isw_not_found_text.visibility = View.GONE
                resultView.visibility = View.VISIBLE
            }
            else {
                resultView.visibility = View.INVISIBLE
                isw_not_found_text.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val TAG = "Arbitration Status FRAGMENT"
    }


}
