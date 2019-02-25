package com.interswitchng.smartpos.modules.ussdqr

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.SelectBankCallback
import com.interswitchng.smartpos.shared.interfaces.library.Payable
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import kotlinx.android.synthetic.main.isw_select_bank_bottom_sheet.*
import org.koin.android.ext.android.inject

class SelectBankBottomSheet: BottomSheetDialogFragment() {

    private val paymentService: Payable by inject()
    private lateinit var adapter: BankListAdapter
    private val bankLists = ArrayList<Bank>()
    private var callback: SelectBankCallback? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        fun newInstance() = SelectBankBottomSheet()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.isw_select_bank_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadBanks()

        proceedSelectBank.setOnClickListener {

            if (!::adapter.isInitialized) return@setOnClickListener

            val selectedBank = adapter.getSelectedBank() ?: return@setOnClickListener
            callback?.onBankSelected(selectedBank)

            dismiss()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as SelectBankCallback
    }

    private fun loadBanks() {
        paymentService.getBanks { allBanks, throwable ->
            if (throwable != null) {
                // TODO handle error
            } else {

                allBanks?.let {
                    it.forEach { bank -> bankLists.add(bank) }
                    handler.post {
                        progressBarSelectBank.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun setupUI() {

        adapter = BankListAdapter(bankLists)
        rvBankLists.layoutManager = GridLayoutManager(activity, 3)
        rvBankLists.adapter = adapter

        adapter.tapListener = { if (proceedSelectBank.visibility != View.VISIBLE) proceedSelectBank.visibility = View.VISIBLE }

        closeSheetButton.setOnClickListener {
            dismiss()
        }
    }
}