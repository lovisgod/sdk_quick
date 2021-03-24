package com.interswitchng.smartpos.modules.main.transfer.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.interswitchng.smartpos.modules.main.transfer.repo.RealmRepo
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

internal class TransactionHistoryViewmodel(
        private val store: KeyValueStore,
        private val realmRepo: RealmRepo):  RootViewModel() {

    private val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(store)!! }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading



    private val _transactions = MutableLiveData<PagedList<TransactionLog>>()
    val transactions: LiveData<PagedList<TransactionLog>> = _transactions





    fun getTransactionHistory(date: Date) {
        try {
            val response = realmRepo.getTransactionHistory(date)
            response.let {
                println("this is getting here")
                println(it)
                _transactions.postValue(it.value)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }

    }
}