package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.modules.main.transfer.adapters.TransactionHistoryAdaptar
import com.interswitchng.smartpos.modules.main.transfer.customdailog
import com.interswitchng.smartpos.modules.main.transfer.hide
import com.interswitchng.smartpos.modules.main.transfer.reveal
import com.interswitchng.smartpos.modules.main.transfer.viewmodels.TransactionHistoryViewmodel
import com.interswitchng.smartpos.modules.menu.report.ReportViewModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.isw_activity_report.*
import kotlinx.android.synthetic.main.isw_fragment_transaction_history.*
import kotlinx.android.synthetic.main.isw_fragment_transaction_history.rvTransactions
import kotlinx.android.synthetic.main.isw_fragment_transaction_history.tvDate
import kotlinx.android.synthetic.main.isw_fragment_transaction_history.tvResultHint
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class TransactionHistoryFragment : BaseFragment(TAG), TransactionHistoryAdaptar.TransactionHistoryItemClickListener,  DatePickerDialog.OnDateSetListener {

    private lateinit var adapter: TransactionHistoryAdaptar
    private var selectedDate = Date()

    // inject viewmodel
    private val viewmodel: TransactionHistoryViewmodel by viewModel()
    private val reportViewmodel: ReportViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_fragment_transaction_history

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupUI()
        showHistoryFor(Date())
    }

    private fun setData(date: Date) {
        val dailog = customdailog(context = this.requireContext(), message = "Loading History")
        viewmodel.getTransactionHistory(date)
        reportViewmodel.getReport(date, TransactionType.Transfer).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dailog.dismiss()
            println(it)
            it?.let {
                println(it)
//                adapter.setData(it as ArrayList<TransactionLog>)
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
                rvTransactions.reveal()
                tvResultHint.hide()
            }
        })
//        viewmodel.transactions.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            dailog.dismiss()
//            println(it)
//            it?.let {
//                println(it)
////                adapter.setData(it as ArrayList<TransactionLog>)
//                adapter.submitList(it)
//                adapter.notifyDataSetChanged()
//                rvTransactions.reveal()
//            }
//        })
    }

    private fun setupAdapter() {
        adapter = TransactionHistoryAdaptar(this)
        rvTransactions.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        rvTransactions.adapter = adapter
    }

    private fun setupUI() {
        // set click listener date selector
        btnSelectDate_.setOnClickListener {
            val dialog = DialogUtils.createDateDialog(requireContext(), this, selectedDate)
            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = TransactionHistoryFragment()
        val TAG = "Transaction History Fragment"
    }

    override fun onclick(data: TransactionResult) {

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // extract the selected date
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // show history for selected date
        showHistoryFor(calendar.time)
    }

    private fun showHistoryFor(time: Date?) {
        if (time != null) {
            selectedDate = time
            // set the date string
            tvDate.text = DateUtils.shortDateFormat.format(time)
            setData(time)
        }
    }


}