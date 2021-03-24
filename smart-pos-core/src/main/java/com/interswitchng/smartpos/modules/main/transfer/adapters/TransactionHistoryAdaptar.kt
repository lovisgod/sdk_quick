package com.interswitchng.smartpos.modules.main.transfer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import java.util.*
import kotlin.collections.ArrayList

class TransactionHistoryAdaptar(val clickListener: TransactionHistoryItemClickListener):
        PagedListAdapter<TransactionLog, TransactionHistoryAdaptar.TransactionHistoryViewHolder>(DiffUtilCallBack()) {

//    private var data = ArrayList<TransactionLog>()

    class TransactionHistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
         val tvAmount: TextView = view.findViewById(R.id.tvAmount)
         val tvTxnType: TextView = view.findViewById(R.id.tvTransactionType)
         val tvPaymentType: TextView = view.findViewById(R.id.tvPaymentType)
         val tvDate: TextView = view.findViewById(R.id.tvDate)
         val container : LinearLayout = view.findViewById(R.id.item_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        // inflate view based on type
        val view =  inflater.inflate(R.layout.isw_list_item_transaction, parent, false)

        // return view holder based on type
        return TransactionHistoryViewHolder(view)
    }

//    override fun getItemCount(): Int {
//        return if (data.size == 0) {
//            0
//        } else {
//            data.size
//        }
//    }

    override fun onBindViewHolder(holder: TransactionHistoryViewHolder, position: Int) {
        val data = getItem(position)
        data?.toResult().let {
            if (it != null) {
                holder.tvAmount.text = holder.tvAmount.context.getString(R.string.isw_currency_amount,
                        it?.amount?.toInt().let { DisplayUtils.getAmountString(it!!) })

                val txnTypeString = when {
                    it?.type?.name == TransactionType.CashOutPay.name -> "Completion"
                    it.type.name == TransactionType.Transfer.name -> "Cash-Out"
                    else -> "Inquiry"
                }
                holder.tvTxnType.text = txnTypeString
                holder.tvPaymentType.text = it.paymentType.name.toString()

                val date = Date(it.time)
                holder.tvDate.text = DateUtils.timeOfDateFormat.format(date)

                val isSuccess = it.responseCode == IsoUtils.OK
                val textColor =
                        if (isSuccess) R.color.iswTextColorSuccessDark
                        else R.color.iswTextColorError

                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.tvAmount.context, textColor))
            }

        }

        holder.container.setOnClickListener {
            data?.toResult()?.let { it1 -> clickListener.onclick(it1) }
        }
    }


//    fun setData(data: ArrayList<TransactionLog>) {
//        this.data = data
//        notifyDataSetChanged()
//    }

     interface TransactionHistoryItemClickListener {
         fun onclick(data: TransactionResult)
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<TransactionLog>() {
        override fun areItemsTheSame(firstLog: TransactionLog, secondLog: TransactionLog): Boolean {
            return firstLog.id == secondLog.id
        }

        override fun areContentsTheSame(firstLog: TransactionLog, secondLog: TransactionLog): Boolean {
            return firstLog.equals(secondLog)
        }

    }
}