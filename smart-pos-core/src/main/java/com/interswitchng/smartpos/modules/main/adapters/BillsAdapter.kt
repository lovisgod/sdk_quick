package com.interswitchng.smartpos.modules.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.bills.models.BillModel
import kotlinx.android.synthetic.main.isw_item_bills_cateogory.view.*

class BillsAdapter(
    val bills: ArrayList<BillModel>,
    val context: Context?,
    val itemClickListener: BillsItemClickListener
) : RecyclerView.Adapter<BillsAdapter.ViewHolder>() {

    interface BillsItemClickListener {
        fun onBillsItemClicked(item: BillModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.category?.text = bills.get(position).name

        holder.itemView.setOnClickListener {
            itemClickListener.onBillsItemClicked(bills.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(LayoutInflater.from(context).inflate(R.layout.isw_item_bills_cateogory, parent, false))
        return vh
    }

    override fun getItemCount(): Int {
        return bills.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category = itemView.isw_category

    }
}
