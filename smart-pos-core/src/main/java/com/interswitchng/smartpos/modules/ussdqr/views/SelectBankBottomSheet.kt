package com.interswitchng.smartpos.modules.ussdqr.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.ussdqr.adapters.BankListAdapter
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import kotlinx.android.synthetic.main.isw_select_bank_bottom_sheet.*

internal class SelectBankBottomSheet : BottomSheetDialogFragment() {

    private var callback: SelectBankCallback? = null
    private val adapter: BankListAdapter = BankListAdapter {
        if (proceedSelectBank.visibility != View.VISIBLE)
            proceedSelectBank.visibility = View.VISIBLE
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val sheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout? = sheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.isw_select_bank_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as SelectBankCallback
    }

    private fun setupUI() {
        rvBankLists.layoutManager = GridLayoutManager(activity, 3)
        rvBankLists.adapter = adapter
        closeSheetButton.setOnClickListener { dismiss() }

        loadBanks()

        proceedSelectBank.setOnClickListener {
            adapter.selectedBank?.also {
                // invoke callback with selected bank
                callback?.onBankSelected(it)
                dismiss()
            } ?: toast("Please select a Bank") // else prompt user to select bank
        }
    }

    private fun loadBanks() {

        callback?.loadBanks { banks ->
            adapter.setBanks(banks)
            progressBarSelectBank.visibility = View.GONE
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance() = SelectBankBottomSheet()
    }

    internal interface SelectBankCallback {
        fun onBankSelected(bank: Bank)

        fun loadBanks(callback: (List<Bank>) -> Unit)
    }
}