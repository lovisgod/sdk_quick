package com.interswitchng.smartpos.modules.ussdqr

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank

internal class BankListAdapter(private var tapListener: () -> Unit): RecyclerView.Adapter<BankListAdapter.BankViewHolder>() {

    private var banks: List<Bank> = emptyList()
    var selectedBank: Bank? = null

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): BankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.isw_list_item_bank, parent, false)
        return BankViewHolder(view)
    }

    override fun getItemCount() = banks.size

    override fun onBindViewHolder(holder: BankViewHolder, p1: Int) {
        val bank = banks[p1]
        holder.bind(bank)
    }

    fun setBanks(banks: List<Bank>) {
        this.banks = banks
        notifyDataSetChanged()
    }

    private fun notifyItemsChanged(prev: Int, new: Int) {
        notifyItemChanged(prev)
        notifyItemChanged(new)
    }

    inner class BankViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val nameTextView by lazy { view.findViewById<TextView>(R.id.bankNameTextView) }
        private val bankImageView by lazy { view.findViewById<ImageView>(R.id.bankImageView) }
        private val isSelectedView by lazy { view.findViewById<View>(R.id.bankSelectionIndicator) }


        fun bind(bank: Bank) {
            nameTextView.text = bank.name
            bankImageView.setOnClickListener {
                tapListener()
                val prev = banks.indexOf(selectedBank)
                selectedBank = bank
                notifyItemsChanged(prev, adapterPosition)
            }

            val bankSelectedState = if (bank == selectedBank) View.VISIBLE else View.GONE
            isSelectedView.visibility = bankSelectedState
        }
    }
}