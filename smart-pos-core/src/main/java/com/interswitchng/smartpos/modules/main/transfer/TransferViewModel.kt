package com.interswitchng.smartpos.modules.main.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.Optional
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryRequestHeaderModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.modules.ussdqr.viewModels.BaseViewModel
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.interfaces.library.SaturnService
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class TransferViewModel(transferService: HttpService, service: SaturnService): BaseViewModel(transferService) {
    val _service = service
    private val _allBanks = MutableLiveData<Optional<List<Bank>>>()
    val allBanks: LiveData<Optional<List<Bank>>>
                        get() = _allBanks

    private val _beneficiary = MutableLiveData<Optional<BeneficiaryModel>>()
    val beneficiary: LiveData<Optional<BeneficiaryModel>>
                        get() = _beneficiary


    fun validateBankDetails(bankCode: String, accountNumber:String) {
        uiScope.launch {
            val beneficiary =  uiScope.async(Dispatchers.IO) { _service.nameEnquiry(bankCode, accountNumber)  }
            _beneficiary.value = beneficiary.await()
        }
    }
}