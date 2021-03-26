package com.interswitchng.smartpos.modules.main.billPayment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillDisplayDataModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener

class cableTvBillerCategoryAdapter(private val callBackListener: NetworkListCallBackListener<BillDisplayDataModel>): RecyclerView.Adapter<cableTvBillerCategoryAdapter.ViewHolder>() {
    var list: ArrayList<BillDisplayDataModel> = ArrayList()
    var filteredItems = ArrayList<BillDisplayDataModel>()

    init {
        filteredItems = list
    }


    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults? {
                val charString = charSequence.toString()
                filteredItems = if(charString.isEmpty()) {
                    list
                } else {
                    val filteredList = arrayListOf<BillDisplayDataModel>()
                    for (row in list) {
                        if (row.title.toLowerCase().contains(charString.toLowerCase()) || row.subtitle.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredItems = filterResults.values as ArrayList<BillDisplayDataModel>
                notifyDataSetChanged()
            }
        }
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container = itemView.findViewById<ConstraintLayout>(R.id.biller_item_container)
        fun bindItems(item: BillDisplayDataModel) {
            val titleContainer = itemView.findViewById(R.id.biller_item_title) as TextView
            val subTitleContainer = itemView.findViewById(R.id.biller_item_desc) as TextView

            titleContainer.text = item.title
            subTitleContainer.text = item.subtitle

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.isw_biller_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (filteredItems.isEmpty()) {
            0
        } else {
            filteredItems.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems.get(position)
        item?.let {
            holder.bindItems(it)
            holder.container.setOnClickListener {
                callBackListener.onDataReceived(item)
            }
        }
    }

    fun setData(list: ArrayList<BillDisplayDataModel>) {
        if (!list.isNullOrEmpty()) {
            this.list = list
            filteredItems = list
            notifyDataSetChanged()
        }
    }
}