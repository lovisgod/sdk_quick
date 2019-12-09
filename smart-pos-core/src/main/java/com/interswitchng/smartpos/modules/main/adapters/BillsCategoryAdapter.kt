package com.interswitchng.smartpos.modules.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.bills.models.BillCategoryModel
import kotlinx.android.synthetic.main.isw_item_bills_cateogory.view.*

class BillsCategoryAdapter(
    val bills: ArrayList<BillCategoryModel>,
    val context: Context?,
    val itemClickListener: BillCategoryItemClickListener
) : RecyclerView.Adapter<BillsCategoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.category?.text = bills.get(position).name

        holder.itemView.setOnClickListener {
            itemClickListener.navigateToBillsFragment(bills.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(LayoutInflater.from(context).inflate(R.layout.isw_item_bills_cateogory, parent, false))
        return vh
    }

    override fun getItemCount(): Int {
        return bills.size
    }

    interface BillCategoryItemClickListener {
        fun navigateToBillsFragment(item: BillCategoryModel)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category = itemView.isw_category

    }
}
