package com.interswitchng.smartpos.modules.menu.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.services.iso8583.utils.FileUtils
import com.interswitchng.smartpos.shared.services.kimono.models.TerminalInformation
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import java.io.InputStream

internal  class SettingsViewModel : RootViewModel(), KoinComponent {

    private val _keysDownloadSuccess = MutableLiveData<Boolean>()
    val keysDownloadSuccess: LiveData<Boolean> = _keysDownloadSuccess

    private val _configDownloadSuccess = MutableLiveData<Boolean>()
    val configDownloadSuccess: LiveData<Boolean> = _configDownloadSuccess



    fun downloadKeys(terminalId: String, ip: String, port: Int, isKimono: Boolean) {
        val isoService: IsoService = get { parametersOf(isKimono) }
        uiScope.launch {
            val isSuccessful = withContext(ioScope) { isoService.downloadKey(terminalId, ip, port) }
            _keysDownloadSuccess.value = isSuccessful
        }
    }

    fun downloadTerminalConfig(terminalId: String, ip: String, port: Int, isKimono: Boolean) {
        val isoService: IsoService = get { parametersOf(isKimono) }
        uiScope.launch {
            val isSuccessful = withContext(ioScope) { isoService.downloadTerminalParameters(terminalId, ip, port) }
            _configDownloadSuccess.value = isSuccessful
        }
    }


    fun getTerminalInformation(xmlFile: InputStream): TerminalInformation = FileUtils.readXml(TerminalInformation::class.java, xmlFile)

}