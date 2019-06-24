package com.interswitchng.smartpos.modules.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class HistoryViewModel(factory: TransactionDataSourceFactory): ViewModel() {

    private val executor: Executor
    val loadingState: LiveData<Boolean>
    val pagedList: LiveData<PagedList<TransactionLog>>


    init {
        // get data loading state from dataSource
        loadingState = Transformations.switchMap(factory.dataSource) {
            it.loadingState
        }

        // setup thread executor and paged list config
        executor = Executors.newFixedThreadPool(3)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPageSize(10)
                .build()

        // setup paged list liveData
        pagedList = LivePagedListBuilder(factory, config)
                .setFetchExecutor(executor).build()

    }
}