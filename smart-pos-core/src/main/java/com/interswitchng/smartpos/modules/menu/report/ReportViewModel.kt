package com.interswitchng.smartpos.modules.menu.report

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import java.util.*

class ReportViewModel(private val transactionLogService: TransactionLogService) : ViewModel() {

    // setup paged list config
    private val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .setPageSize(10)
            .build()

    fun getReport(day: Date): LiveData<PagedList<TransactionLog>> {
        return transactionLogService.getTransactionFor(day, config)
    }
}