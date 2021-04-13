package com.interswitchng.smartpos.modules.menu.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.IswTxnHandler
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.FileUtils
import com.interswitchng.smartpos.shared.services.kimono.models.AgentIdResponse
import com.interswitchng.smartpos.shared.services.kimono.models.AllTerminalInfo
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

    private val _agentInfo = MutableLiveData<AgentIdResponse>()
    val agentInfo: LiveData<AgentIdResponse> = _agentInfo


    private val _terminalConfigResponse = MutableLiveData<AllTerminalInfo>()
    val terminalConfigResponse: LiveData<AllTerminalInfo> = _terminalConfigResponse

    fun downloadTerminalParameters(serialNumber: String, isKimono: Boolean) {
        val isoService: IsoService = get { parametersOf(isKimono) }
        uiScope.launch {
            val response = withContext(ioScope) { IswTxnHandler().downloadTmKimParam(serialNumber) }
            _terminalConfigResponse.value = response
            println("Settings ViewModel : $terminalConfigResponse")
        }
    }

    fun downloadKeys(terminalId: String, ip: String, port: Int, isKimono: Boolean, isNibbsTest: Boolean) {
        val isoService: IsoService = get { parametersOf(isKimono) }
        uiScope.launch {
            val isSuccessful = withContext(ioScope) {
                isoService.downloadKey(terminalId, ip, port, isNibbsTest)
            }
            _keysDownloadSuccess.value = isSuccessful
        }
    }

    fun downloadTerminalConfig(terminalId: String, ip: String, port: Int, isKimono: Boolean) {
        val isoService: IsoService = get { parametersOf(isKimono) }
        uiScope.launch {
            val isSuccessful = withContext(ioScope) { isoService.downloadTerminalParameters(terminalId, ip, port) }
            _configDownloadSuccess.value = isSuccessful
            //Logger.with("Settings ViewModel").logErr(isoService.downloadTerminalParameters(terminalId,ip,port).toString())
        println("Settings ViewModel : $isSuccessful")
    }
    }

    fun downloadAgentInfoDownload(terminalId: String, isKimono: Boolean) {
        val isoService: IsoService = get { parametersOf(isKimono) }
        uiScope.launch {
            val agentResponse = withContext(ioScope) { isoService.downloadAgentId(terminalId) }
            _agentInfo.value = agentResponse
            //Logger.with("Settings ViewModel").logErr(isoService.downloadTerminalParameters(terminalId,ip,port).toString())
            println("Settings ViewModel : $agentResponse")
        }
    }


    fun getTerminalInformation(xmlFile: InputStream): TerminalInformation = FileUtils.readXml(TerminalInformation::class.java, xmlFile)

    fun getTerminalInfoFromResponse(info : AllTerminalInfo): TerminalInfo{
        return TerminalInfo(
                terminalId = info.terminalInfoBySerials?.terminalCode.toString(),
                merchantId = info.terminalInfoBySerials?.merchantId.toString(),
                merchantNameAndLocation = info.terminalInfoBySerials?.cardAcceptorNameLocation.toString(),
                merchantCategoryCode = info.terminalInfoBySerials?.merchantCategoryCode.toString(),
                countryCode = Constants.ISW_COUNTRY_CODE,
                currencyCode = Constants.ISW_CURRENCY_CODE,
                callHomeTimeInMin = Constants.ISW_CALL_HOME_TIME_IN_MIN.toIntOrNull() ?: -1,
                serverTimeoutInSec = Constants.ISW_SERVER_TIMEOUT_IN_SEC.toIntOrNull() ?: -1,
                isKimono = true,
                capabilities = Constants.ISW_TERMINAL_CAPABILITIES,
                serverIp = Constants.ISW_TERMINAL_IP,
                serverUrl = Constants.ISW_KIMONO_BASE_URL,
                serverPort = Constants.ISW_CTMS_PORT.toIntOrNull() ?: -1,
                agentId = info.terminalInfoBySerials?.merchantPhoneNumber.toString(),
                agentEmail = info.terminalInfoBySerials?.merchantEmail.toString()
        )
    }

}