package com.interswitchng.smartpos.shared.services.storage

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.zhuinden.monarchy.Monarchy
import io.realm.Sort
import io.realm.kotlin.where
import java.util.*


class TransactionLogServiceImpl(private val monarchy: Monarchy) : TransactionLogService {

    override fun logTransactionResult(result: TransactionLog) = monarchy.writeAsync { realm ->
        // retrieve the latest id
        val currentId: Number = realm.where(TransactionLog::class.java).max("id") ?: 0
        // set the next id
        result.id = currentId.toInt() + 1
        // save result to realm db
        realm.copyToRealm(result)
    }

    override fun getTransactions(pagedListConfig: PagedList.Config): LiveData<PagedList<TransactionLog>> {

        // query for stream of transaction logs by creating dataSource factory
        val logDataSourceFactory = monarchy.createDataSourceFactory { realm ->
            realm.where<TransactionLog>().sort("time", Sort.DESCENDING)
        }

        // create paged list builder for datasource factory
        val livePagedBuilder = LivePagedListBuilder<Int, TransactionLog>(logDataSourceFactory, pagedListConfig)

        // generate and return liveData of pagedList transactionLog
        return monarchy.findAllPagedWithChanges(logDataSourceFactory, livePagedBuilder)
    }

    override fun getTransactionFor(date: Date, pagedListConfig: PagedList.Config): LiveData<PagedList<TransactionLog>> {

        // calendar to generate first hour of day
        val morningCalendar = Calendar.getInstance()
        // get 12AM (00:00:00 AM) in the morning
        val morning = morningCalendar.apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // calendar to generate last hour of day
        val nightCalendar = Calendar.getInstance()
        // get midnight 11:59PM (23:59:60 PM) in the midnight
        val midnight = nightCalendar.apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }


        // query for stream of transaction logs for specified day by creating dataSource factory
        val logDataSourceFactory = monarchy.createDataSourceFactory { realm ->
            realm.where<TransactionLog>()
                    .greaterThan("time", morning.timeInMillis)
                    .lessThan("time", midnight.timeInMillis)
                    .sort("time", Sort.DESCENDING)
        }

        // create paged list builder for datasource factory
        val livePagedBuilder = LivePagedListBuilder<Int, TransactionLog>(logDataSourceFactory, pagedListConfig)

        // generate and return liveData of pagedList transactionLog
        return monarchy.findAllPagedWithChanges(logDataSourceFactory, livePagedBuilder)

    }

}