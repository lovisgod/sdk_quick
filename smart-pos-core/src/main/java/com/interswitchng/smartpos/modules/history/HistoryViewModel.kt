package com.interswitchng.smartpos.modules.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class HistoryViewModel(transactionLogService: TransactionLogService): ViewModel() {

    // history paged list
    val pagedList: LiveData<PagedList<TransactionLog>>


    init {
        // setup thread executor and paged list config
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .build()

        // setup paged list liveData
        pagedList = transactionLogService.getTransactions(config)
    }
}