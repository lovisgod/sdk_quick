package com.interswitchng.smartpos.modules.main.billPayment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworksModel

class NetworkRecyclerAdapter(val networkList: ArrayList<NetworksModel>, private val callBackListener: NetworkListCallBackListener<NetworksModel>): RecyclerView.Adapter<NetworkRecyclerAdapter.ViewHolder>() {
    var viewHolderList = arrayListOf<ViewHolder>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.isw_network_list_card, parent, false)
        viewHolderList.add(ViewHolder(v))
        return ViewHolder(v)
    }

    fun updateBackground() {
        for(viewHolder in viewHolderList) {
            if (viewHolder !=null) {
               val z = viewHolder.itemView.findViewById(R.id.isw_netowrk_card_container) as FrameLayout
               z.setBackgroundResource(0)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(networkList[position])
        holder.itemView.setOnClickListener {
            updateBackground()
            callBackListener.onDataReceived(networkList[position])
            val frame = holder.itemView.findViewById(R.id.isw_netowrk_card_container) as FrameLayout
            frame.setBackgroundResource(R.drawable.isw_recharge_networks_bg)
        }
    }

    override fun getItemCount(): Int {
        return networkList.size
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var previous: FrameLayout? = null
        fun bindItems(network: NetworksModel) {
            val imageContainer = itemView.findViewById(R.id.isw_networks_image) as ImageView
            val imageResource = network.networkLogoPath
            imageContainer.setImageResource(imageResource)
        }
    }
}