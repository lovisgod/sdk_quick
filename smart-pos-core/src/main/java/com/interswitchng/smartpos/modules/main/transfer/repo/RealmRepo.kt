package com.interswitchng.smartpos.modules.main.transfer.repo

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import java.util.*

class RealmRepo(val logService: TransactionLogService) {
    // setup paged list config
    private val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(50)
            .setPageSize(50)
            .build()

    fun saveLog(log: TransactionLog) {
        logService.logTransactionResult(log)
    }

    fun getTransactionHistory(date: Date): LiveData<PagedList<TransactionLog>> {
       val response  = logService.getTransactionFor(date, TransactionType.Transfer, config)
        println(response.value?.get(0))
        return response
    }
}