package com.interswitchng.smartpos.shared.interfaces.library

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import java.util.*

interface TransactionLogService {


    /**
     * This function is responsible for saving transaction
     * logs to the proposed storage implementation
     *
     * @param result  The transaction result to be logged
     */
    fun logTransactionResult(result: TransactionLog)


    /**
     * This function is responsible for retrieving all logged
     * transaction, essentially providing transaction history
     *
     * @param  pagedListConfig  a configuration object for the returned paged list
     * @return  a paged list of all the transactions that have been stored
     */
    fun getTransactions(pagedListConfig: PagedList.Config): LiveData<PagedList<TransactionLog>>


    /** This function is responsible for retrieving transaction for a
     * specific day, with the specified range of start to end of day
     *
     * @param date  the day for transactions to be retrieved
     * @return  a paged list of transactions for that day
     */
    fun getTransactionFor(date: Date, pagedListConfig: PagedList.Config): LiveData<PagedList<TransactionLog>>

}