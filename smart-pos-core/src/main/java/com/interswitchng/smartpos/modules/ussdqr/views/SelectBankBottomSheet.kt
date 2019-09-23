package com.interswitchng.smartpos.modules.ussdqr.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.ussdqr.adapters.BankListAdapter
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import kotlinx.android.synthetic.main.isw_select_bank_bottom_sheet.*

internal class SelectBankBottomSheet : BottomSheetDialogFragment() {

    private val adapter: BankListAdapter = BankListAdapter {
        if (selectBank.visibility != View.VISIBLE)
            selectBank.visibility = View.VISIBLE
    }

    private val _selectedBank = MutableLiveData<Bank>()
    val selectedBank: LiveData<Bank> get() = _selectedBank

    var hasBanks = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val sheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
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

    private fun setupUI() {
        rvBankLists.layoutManager = GridLayoutManager(activity, 3)
        rvBankLists.adapter = adapter
        closeSheetButton.setOnClickListener { dismiss() }

        // set visible if adpater has no banks
        progressBarSelectBank.visibility =
                if (adapter.itemCount > 0) View.GONE
                else View.VISIBLE

        // set click listener for adapter's selected bank§
        selectBank.setOnClickListener {
            adapter.selectedBank?.also {
                // set the selected bank
                _selectedBank.value = it
                dismiss()
            } ?: toast("Please select a Bank") // else prompt user to select bank
        }
    }

    fun loadBanks(banks: List<Bank>) {
        hasBanks = banks.isNotEmpty()
        adapter.setBanks(banks)
        progressBarSelectBank.visibility = View.GONE
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance() = SelectBankBottomSheet()
    }
}