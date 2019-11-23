package com.interswitchng.smartpos.modules.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import kotlinx.android.synthetic.main.isw_activity_home_list_item.view.*
import java.util.*

class ActivityAdapter : PagedListAdapter<TransactionLog, RecyclerView.ViewHolder>(diffCallback) {

    lateinit var itemClickListener: ActivityItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.isw_activity_home_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        item?.toResult()?.apply {
            holder.itemView.isw_txn_type.text = type.name
            holder.itemView.isw_txn_amount.let {
                it.text = it.context.getString(R.string.isw_currency_amount, this.amount)
            }

            val date = Date(item.time)
            holder.itemView.isw_txn_date.text = DateUtils.timeOfDateFormat.format(date)

        }
    }

    fun setOnActivityItemClickListener(activityItemClickListener: ActivityItemClickListener) {
        itemClickListener = activityItemClickListener
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = getItem(adapterPosition)
            itemClickListener.navigateToActivityDetailFragment(item!!)
        }
    }

    interface ActivityItemClickListener {
        fun navigateToActivityDetailFragment(item: TransactionLog)
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<TransactionLog>() {

            override fun areItemsTheSame(firstLog: TransactionLog, secondLog: TransactionLog): Boolean {
                return firstLog.id == secondLog.id
            }

            override fun areContentsTheSame(firstLog: TransactionLog, secondLog: TransactionLog): Boolean {
                return firstLog.equals(secondLog)
            }

        }
    }
}
