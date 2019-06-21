package com.interswitchng.smartpos.shared.viewmodel

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class RootViewModel: ViewModel() {

    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)
    protected val ioScope = uiScope.coroutineContext + Dispatchers.IO



    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}