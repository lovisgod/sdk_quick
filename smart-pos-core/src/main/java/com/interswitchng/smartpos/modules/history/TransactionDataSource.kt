package com.interswitchng.smartpos.modules.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class TransactionDataSource: PageKeyedDataSource<Int, TransactionLog>() {

    private val _loading = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loading

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, TransactionLog>) {
        _loading.postValue(true)
        val result = load(1, params.requestedLoadSize)
        callback.onResult(result, null, 2)
        _loading.postValue(false)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionLog>) {
        _loading.postValue(true)
        val result = load(params.key, params.requestedLoadSize)
        val nextPage = params.key + 1
        callback.onResult(result, nextPage)
        _loading.postValue(false)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionLog>) {}


    private fun load(page: Int, size: Int): List<TransactionLog> {

        val start = ((page - 1) * size) + 1
        val end = (start + size) - 1

        if (end > 50) return emptyList()

        // simulate time delay
        runBlocking { delay(4000) }

        val list = mutableListOf<TransactionLog>()
        for(i in start..end) {
            val log = TransactionLog(id = i, amount = "$i.00", dateTime = "Mon $i Jun, 2019")
            list.add(log)
        }

        return list
    }
}