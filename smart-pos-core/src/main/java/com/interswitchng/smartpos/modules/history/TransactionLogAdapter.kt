package com.interswitchng.smartpos.modules.history

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog

class TransactionLogAdapter : PagedListAdapter<TransactionLog, RecyclerView.ViewHolder>(diffCallback) {

    private var loading: Boolean? = null

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        // inflate view based on type
        val view = if (type == TYPE_LOADING) inflater.inflate(R.layout.isw_list_item_loaing, parent, false)
        else inflater.inflate(R.layout.isw_list_item_transaction, parent, false)

        // return view holder based on type
        return if (type == TYPE_LOADING) LoadingViewHolder(view)
        else TransactionViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is LoadingViewHolder -> viewHolder.bind(loading ?: false)
            is TransactionViewHolder -> viewHolder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        // has extra row if its loading
        val hasExtraRow = loading != false
        // is loading if hasExtra row and position is at last item
        val isLoading = hasExtraRow && position == itemCount - 1

        // return view type based on loading status
        return if (isLoading) TYPE_LOADING
        else TYPE_ITEM
    }


    fun setLoadingStatus(newStatus: Boolean?) {
        // get previous loading state
        val prevLoadingStatus = loading
        val wasLoading = prevLoadingStatus != false

        // check next loading state
        loading = newStatus
        val isLoading = loading != false

        // react to extra data flag changing
        // between loading updates
        if (wasLoading != isLoading) {
            // if it previously was loading data
            // and now has loaded data, notify [loader] item removed
            if (wasLoading) notifyItemRemoved(itemCount)
            // else if it previously wasn't loading data
            // and now it has more data, notify [loader] item added
            else notifyItemInserted(itemCount)
        } else if (isLoading && prevLoadingStatus != loading) {
            notifyItemInserted(itemCount - 1)
        }
    }


    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        private val tvTxnType: TextView = view.findViewById(R.id.tvTransactionType)
        private val tvPaymentType: TextView = view.findViewById(R.id.tvPaymentType)
        private val tvDate: TextView = view.findViewById(R.id.tvDate)


        fun bind(txn: TransactionLog?) {
            txn?.toResult()?.apply {
                tvAmount.text = amount
                tvTxnType.text = type.name
                tvPaymentType.text = paymentType.name
                tvDate.text = dateTime
            }
        }
    }


    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val progressBar: ProgressBar = view.findViewById(R.id.pbTransaction)


        fun bind(loadingStatus: Boolean) {
            val visibility =
                    if (loadingStatus) View.VISIBLE
                    else View.GONE
            progressBar.visibility = visibility

        }
    }

    companion object {

        private const val TYPE_LOADING = -1
        private const val TYPE_ITEM = 1

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