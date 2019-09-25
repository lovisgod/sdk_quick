package com.interswitchng.smartpos.modules.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.ActivityModel
import kotlinx.android.synthetic.main.isw_activity_home_list_item.view.*

class ActivityAdapter : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    val adapterItems: MutableList<ActivityModel> by lazy {
        val listOfItems = mutableListOf<ActivityModel>()

        addAdapterItems(listOfItems)

        listOfItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.isw_activity_home_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = adapterItems[position]

        holder.itemView.isw_customer_name.text = item.customerName
        holder.itemView.isw_transaction_type.text = item.transactionType
        holder.itemView.isw_transaction_amount.text = item.transactionAmount
    }

    override fun getItemCount() = adapterItems.size

    private fun addAdapterItems(items: MutableList<ActivityModel>) {
        items.add(ActivityModel("M, Aregbede", "Direct Payment", "N1,000,000"))
        items.add(ActivityModel("A, Samuel", "Ussd", "N2,000,000"))
        items.add(ActivityModel("A, Paul", "Direct payment", "N2,000,000"))
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            v?.findNavController()?.navigate(R.id.isw_goto_activity_detail_fragment_action)
        }
    }
}
