package com.interswitchng.smartpos.modules.ussdqr.adapters

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.views.TweakableOutlineProvider

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

        private val context by lazy { view.context }
        private val nameTextView by lazy { view.findViewById<TextView>(R.id.bankNameTextView) }
        private val bankImageView by lazy { view.findViewById<ImageView>(R.id.bankImageView) }


        fun bind(bank: Bank) {
            val baseUrl = context.getString(R.string.ISW_IMAGE_BASE_URL)
            val imageUrl = "${baseUrl}banks/${bank.code}.png"

            // load image
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.isw_bank_placeholder)
                    .fitCenter()
                    .into(bankImageView)


            nameTextView.text = bank.name

            if (bank == selectedBank) {
                // add tint or elevation
                val filter: Int = ContextCompat.getColor(context, R.color.iswTransparent)
                if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) bankImageView.setColorFilter(filter)
                else bankImageView.apply {
                    elevation = context.resources.getDimension(R.dimen.isw_elevation)
                    outlineProvider = TweakableOutlineProvider()
                    setBackgroundColor(ContextCompat.getColor(context, R.color.iswColorPrimary))
                }
            } else {
                // remove tint or elevation
                val filter: Int = ContextCompat.getColor(context, android.R.color.transparent)
                if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) bankImageView.setColorFilter(filter)
                else bankImageView.elevation = 0f
            }

            bankImageView.setOnClickListener {
                tapListener()
                val prev = banks.indexOf(selectedBank)
                selectedBank = bank
                notifyItemsChanged(prev, adapterPosition)
            }
        }
    }
}