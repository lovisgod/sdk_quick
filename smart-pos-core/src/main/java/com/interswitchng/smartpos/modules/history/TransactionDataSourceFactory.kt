package com.interswitchng.smartpos.modules.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog


class TransactionDataSourceFactory: DataSource.Factory<Int, TransactionLog>() {

    private val _dataSource = MutableLiveData<TransactionDataSource>()
    val dataSource: LiveData<TransactionDataSource> get() = _dataSource


    override fun create(): DataSource<Int, TransactionLog> = TransactionDataSource().also {
        _dataSource.postValue(it)
    }

}