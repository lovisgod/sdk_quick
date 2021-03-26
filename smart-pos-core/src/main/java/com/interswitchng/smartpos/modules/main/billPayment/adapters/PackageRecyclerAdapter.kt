package com.interswitchng.smartpos.modules.main.billPayment.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillPaymentCategoriesModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener

class PackageRecyclerAdapter<BC: ArrayList<BillPaymentCategoriesModel>> (val list: BC, private val callBackListener: NetworkListCallBackListener<BillPaymentCategoriesModel>): RecyclerView.Adapter<PackageRecyclerAdapter.ViewHolder>() {

    var filteredItems = ArrayList<BillPaymentCategoriesModel>()

    init {
        filteredItems = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.isw_billpayment_package_category_list, parent, false)
        return ViewHolder(v)
    }



    override fun onBindViewHolder(holder: PackageRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindItems(filteredItems.get(position))
        holder.itemView.setOnClickListener {
            callBackListener.onDataReceived(filteredItems.get(position))
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    fun getFilter(): Filter? {
        return object : Filter() {
             override fun performFiltering(charSequence: CharSequence?): FilterResults? {
                val charString = charSequence.toString()
                 filteredItems = if(charString.isEmpty()) {
                     list
                 } else {
                     val filteredList = arrayListOf<BillPaymentCategoriesModel>()
                     for (row in list) {
                         Log.d("Changing", "I dey try change")
                         if (row.title?.toLowerCase()?.contains(charString.toLowerCase())!! || row.subTitle?.toLowerCase()?.contains(charString.toLowerCase())!!) {
                             filteredList.add(row)
                         }
                     }
                     filteredList as BC
                 }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

             override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredItems = filterResults?.values as BC
                notifyDataSetChanged()
            }
        }
    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: BillPaymentCategoriesModel) {
            val titleContainer = itemView.findViewById(R.id.isw_bill_payment_package_list_title) as TextView
            val subTitleContainer = itemView.findViewById(R.id.isw_bill_payment_package_list_subtitle) as TextView

            titleContainer.text = item.title
            subTitleContainer.text = item.subTitle
        }
    }


}