package com.interswitchng.smartpos.modules.ussdqr

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank

class BankListAdapter(private val banks: List<Bank>): RecyclerView.Adapter<BankListAdapter.BankViewHolder>() {

    var tapListener: () -> Unit = {}

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
            BankViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.isw_bank_view, p0, false))

    override fun getItemCount() = banks.size

    override fun onBindViewHolder(holder: BankViewHolder, p1: Int) {

        val next = banks[p1]
        holder.nameTextView.text = next.name
        val bankSelectedState = if (next.selected) View.VISIBLE else View.GONE

        holder.isSelectedView.visibility = bankSelectedState

        holder.bankImageView.setOnClickListener {
            selectBank(p1)
        }
    }

    private fun selectBank(position: Int) {

        banks.forEach { bank ->
            bank.selected = false
        }

        tapListener()
        banks[position].selected = true
        notifyDataSetChanged()
    }

    fun getSelectedBank(): Bank? {

        val select = selectBanks()
        if (select.isEmpty()) return null

        return select.first()
    }

    private fun selectBanks() = banks.filter { bank -> bank.selected }

    class BankViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val nameTextView by lazy { view.findViewById<TextView>(R.id.bankNameTextView) }
        val bankImageView by lazy { view.findViewById<ImageView>(R.id.bankImageView) }
        val isSelectedView by lazy { view.findViewById<View>(R.id.bankSelectionIndicator) }
    }
}