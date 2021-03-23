package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.adapters.NetworkRecyclerAdapter
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_airtime_recharge_input.*
import kotlinx.android.synthetic.main.isw_transfer_bank_filter_dialog.*

class AirtimeRechargeInputFragment : BaseFragment(TAG), NetworkListCallBackListener<Array<String>, View> {
    val networks = arrayOf<String>("MTN", "GLO", "AIRTEL", "9MOBILE")
    override val layoutId: Int
        get() = R.layout.isw_fragment_airtime_recharge_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNetworkList()
    }

    @SuppressLint("WrongConstant")
    fun loadNetworkList() {
        val recyclerView = isw_recharge_networks_list
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        val adapter = NetworkRecyclerAdapter(networks, this)
        recyclerView.adapter = adapter
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AirtimeRechargeInputFragment()

        val TAG = "AirtimeRechargeInput"
    }

    override fun onDataReceived(data: Array<String>, view: View?) {
//        val frame = view?.findViewById(R.id.isw_netowrk_card_container) as FrameLayout
//        frame.setBackgroundResource(R.drawable.isw_recharge_networks_bg)
    }
}