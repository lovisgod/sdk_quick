package com.interswitchng.smartpos.modules.menu.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SettingsViewModel(private val isoService: IsoService): RootViewModel() {

    private val _keysDownloadSuccess = MutableLiveData<Boolean>()
    val keysDownloadSuccess: LiveData<Boolean> = _keysDownloadSuccess

    private val _configDownloadSuccess = MutableLiveData<Boolean>()
    val configDownloadSuccess: LiveData<Boolean> = _configDownloadSuccess


    fun downloadKeys(terminalId: String) {
        uiScope.launch {
            val isSuccessful = withContext(ioScope) { isoService.downloadKey(terminalId) }
            _keysDownloadSuccess.value = isSuccessful
        }
    }

    fun downloadTerminalConfig(terminalId: String) {
        uiScope.launch {
            val isSuccessful = withContext(ioScope) { isoService.downloadTerminalParameters(terminalId) }
            _configDownloadSuccess.value = isSuccessful
        }
    }
}