package com.interswitchng.smartpos.modules.menu.report

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.MenuActivity
import com.interswitchng.smartpos.shared.adapters.TransactionLogAdapter
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.isw_activity_report.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReportActivity : MenuActivity(), DatePickerDialog.OnDateSetListener {

    private val reportViewModel: ReportViewModel by viewModel()

    private lateinit var adapter: TransactionLogAdapter

    private lateinit var reportLiveData: LiveData<PagedList<TransactionLog>>

    // initialize date as today
    private var selectedDate = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_report)

        // setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // create paging adapter
        adapter = TransactionLogAdapter()

        // setup recycler view
        rvTransactions.adapter = adapter
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        // set click listener date selector
        btnSelectDate.setOnClickListener {
            val dialog = DialogUtils.createDateDialog(this, this, selectedDate)
            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
        }

        // select today's reports
        showReportFor(selectedDate)
    }

    private fun showReportFor(day: Date) {
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
        reportLiveData = reportViewModel.getReport(day)
        reportLiveData.observe(owner){ submitList(it, day) }
    }


    private fun submitList(list: PagedList<TransactionLog>?, day: Date) {

        // hide progress bar
        initialProgress.visibility = View.GONE

        // flag to determine if recycler view has no content
        val hasNoContent = list != null && list.isEmpty() && adapter.itemCount == 0

        // show recycler view based on results
        if (hasNoContent) {
            // hide recycler view and show no result
            rvTransactions.visibility = View.GONE
            tvResultHint.visibility = View.VISIBLE
            // format text for no result
            val date = DateUtils.shortDateFormat.format(day)
            tvResultHint.text = getString(R.string.isw_no_report, date)
        } else {
            // submit paged list to adapter
            adapter.submitList(list)
            // hide no report hint, and show recycler view
            rvTransactions.visibility = View.VISIBLE
            tvResultHint.visibility = View.GONE
        }
    }

    override fun onDateSet(p0: DatePicker?,year: Int, monthOfYear: Int, dayOfMonth: Int) {
        // extract the selected date
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // show report for selected date
        showReportFor(calendar.time)
    }

}
