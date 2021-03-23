package com.interswitchng.smartpos.modules.main.billPayment.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener

class NetworkRecyclerAdapter(val networkList: Array<String>, private val callBackListener: NetworkListCallBackListener<Array<String>, View>): RecyclerView.Adapter<NetworkRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.isw_network_list_card, parent, false)
        v.setOnClickListener {
            callBackListener.onDataReceived(arrayOf(""), v)
        }
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(networkList[position])
    }

    override fun getItemCount(): Int {
        return networkList.size
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var previous: FrameLayout? = null
        fun bindItems(network: String) {
            itemView.setOnClickListener {
//              add the drawable background to the frame
                val frame = itemView.findViewById(R.id.isw_netowrk_card_container) as FrameLayout
                Log.d("Frame Click", frame.id.toString())
                previous.let {
                    if (it != null) {
                        it.setBackgroundResource(0)
                    }
                }
                previous = frame
                frame.setBackgroundResource(R.drawable.isw_recharge_networks_bg)
            }
        }
    }
}