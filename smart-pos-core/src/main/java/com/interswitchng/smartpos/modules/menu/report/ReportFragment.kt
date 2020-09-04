package com.interswitchng.smartpos.modules.menu.report

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.EndOfDayPrintDialog
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.adapters.TransactionLogAdapter
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.isw_activity_report.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReportFragment : BaseFragment(TAG), DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    // setup spinner layout
    // attach listener to update global variable
    // on item, update the recycler view

    private val reportViewModel: ReportViewModel by viewModel()

    private lateinit var adapter: TransactionLogAdapter

    private lateinit var reportLiveData: LiveData<PagedList<TransactionLog>>

    private lateinit var endOfDayPrintDialog: EndOfDayPrintDialog

    private val transactionTypes = arrayOf("All", "Inquiry", "Completion")
    private var transactionType: TransactionType? = null


    // initialize date as today
    private var selectedDate = Date()

    override val layoutId: Int
        get() = R.layout.isw_activity_report


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup the spinner
        val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                transactionTypes
        )

        spinnerAdapter.setDropDownViewResource(R.layout.isw_list_item_transaction_type)
        spinnerTransactionTypes.adapter = spinnerAdapter
        spinnerTransactionTypes.onItemSelectedListener = this


        // create paging adapter
        adapter = TransactionLogAdapter()

        // setup recycler view
        rvTransactions.adapter = adapter
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        // set click listener date selector
        btnSelectDate.setOnClickListener {
            val dialog = DialogUtils.createDateDialog(requireContext(), this, selectedDate)
            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
        }

        isw_btn_clear_eod.setOnClickListener{
            reportViewModel.clearEod(selectedDate)
            spinnerTransactionTypes.visibility = View.GONE
            toast("Data Cleared")
        }

        // select today's reports
        showReportFor(selectedDate, transactionType)

        isw_button_eod.setOnClickListener {
            val transactions = when (val transactionType = transactionType) {
                null -> reportViewModel.getEndOfDay(selectedDate)
                else -> reportViewModel.getEndOfDay(selectedDate, transactionType)
            }


            // observer to print transactions
            lateinit var printObserver: Observer<List<TransactionLog>>

            // create observer to print and remove itself as an observer
            // to simulate one-time observation
            printObserver = Observer {
                it ?: return@Observer

                if (it.isEmpty()) {
                    toast("Nothing to print")
                    isw_button_eod.isEnabled = true
                    isw_button_eod.isClickable = true
                    return@Observer
                }

                // print transaction based on transactionType else print all
                when (val transactionType = transactionType) {
                    null -> reportViewModel.printAll(selectedDate)
                    else -> reportViewModel.printEndOfDay(selectedDate, it, transactionType)
                }
                // remove observer once print has been triggered
                transactions.removeObserver(printObserver)
            }

            // observe the live data
            transactions.observe(viewLifecycleOwner, printObserver)


        }

        // observe view model
        with(reportViewModel) {

            printButton.observe(this@ReportFragment, Observer {
                it ?: return@Observer

                // toggle button's clickable state
                isw_button_eod.isEnabled = it
                isw_button_eod.isClickable = it
            })

            printerMessage.observe(this@ReportFragment, Observer {
                it ?: return@Observer
                toast(it)
            })
        }

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        // get the neame of the transaction type
        val transactionTypeName = transactionTypes[position]
        transactionType = when (transactionTypeName) {
            "Inquiry" -> {
                TransactionType.CashOutInquiry
            }
            "Completion" -> {
                TransactionType.CashOutPay
            }
            else -> {
                null
            }
        }

       /* // get the enum value based on the name
        transactionType =
                if (position == 0) null
                else TransactionType.valueOf(transactionTypeName)*/
        toast(transactionTypeName)

        // update recycler viw
        showReportFor(selectedDate, transactionType)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

    private fun showReportFor(day: Date, transactionType: TransactionType?) {
        // set selected date
        selectedDate = day

        // set the date string
        tvDate.text = DateUtils.shortDateFormat.format(day)

        // show loader and hide recycler view
        initialProgress.visibility = View.VISIBLE
        rvTransactions.visibility = View.GONE

        // clear current report
        if (adapter.itemCount > 0) {
            adapter.currentList?.dataSource?.invalidate()
            adapter.notifyDataSetChanged()
        }

        // lifecycle owner
        val owner = { lifecycle }

        // remove observers from current live data
        if (::reportLiveData.isInitialized)
            reportLiveData.removeObservers(owner)

        // get and observe new report
        reportLiveData = when (transactionType) {
            null -> reportViewModel.getReport(day)
            else -> reportViewModel.getReport(day, transactionType)
        }

        reportLiveData.observe(owner) { submitList(it, day) }
    }


    private fun submitList(list: PagedList<TransactionLog>?, day: Date) {

        // hide progress bar
        initialProgress.visibility = View.GONE

        // flag to determine if recycler view has no content
        val hasNoContent = list != null && list.isEmpty() && adapter.itemCount == 0

        // show recycler view based on results
        if (hasNoContent) {
            // hide recycler view,hide spinner and show no result
            rvTransactions.visibility = View.GONE
            spinnerTransactionTypes.visibility = View.GONE
            tvResultHint.visibility = View.VISIBLE
            // format text for no result
            val date = DateUtils.shortDateFormat.format(day)
            tvResultHint.text = getString(R.string.isw_no_report, date)
        } else {
            // submit paged list to adapter
            adapter.submitList(list)
            // hide no report hint, show spinner, and show recycler view
            rvTransactions.visibility = View.VISIBLE
            spinnerTransactionTypes.visibility = View.VISIBLE
            tvResultHint.visibility = View.GONE
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        // extract the selected date
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // show report for selected date
        showReportFor(calendar.time, transactionType)
    }


    companion object {
        const val TAG = "Report Fragment"
    }
}
