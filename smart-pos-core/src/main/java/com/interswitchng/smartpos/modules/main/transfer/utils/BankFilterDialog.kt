package com.interswitchng.smartpos.modules.main.transfer.utils

import com.interswitchng.smartpos.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.interswitchng.smartpos.modules.main.transfer.adapters.BankAutoCompleteAdapter
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.CallbackListener
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_transfer_bank_filter_dialog.*


class BankFilterDialog(private val callbackListener: CallbackListener): DialogFragment() {
    val TAG = "isw_transfer_bank_filter_dialog"
    private var bankList = arrayListOf<BankModel>()
    private var toolbar: Toolbar? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
                            ): View? {
        isCancelable = false
        super.onCreateView(inflater, container, savedInstanceState)
        bankList.addAll(Constants.BANK_LIST.sortedWith(compareBy { it.bankName }))
        var rootView: View = inflater.inflate(R.layout.isw_transfer_bank_filter_dialog, container, false)
        return rootView
    }

    override fun getTheme(): Int {
        return R.style.ISW_TransferFilterDialog
    }

    fun setUpAutoComplete() {
        val auto: ListView = isw_transfer_bank_search
        val adapter = context?.let {
            BankAutoCompleteAdapter(
                    it,
                    R.layout.isw_bank_autocomplete_row, bankList
            )
        }
        auto.setAdapter(adapter)
        //        TODO: Clear selected bank once the user does a delete activity

        auto.setOnItemClickListener { parent, view, position, id ->
            var selectedBank = parent.getItemAtPosition(position) as BankModel
            callbackListener.onDataReceived(selectedBank)
            dismiss()
        }

        val search: SearchView = isw_transfer_bank_search_bar
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search.clearFocus()
                if (adapter != null) {
                    adapter.filter.filter(p0)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAutoComplete()
        isw_transfer_search_dialog_toolbar.setNavigationOnClickListener(View.OnClickListener() {
            dismiss()
        })

    }
}