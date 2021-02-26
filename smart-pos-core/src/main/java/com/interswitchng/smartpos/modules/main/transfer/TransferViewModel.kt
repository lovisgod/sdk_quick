package com.interswitchng.smartpos.modules.main.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.Optional
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryRequestHeaderModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.modules.ussdqr.viewModels.BaseViewModel
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class TransferViewModel(service: HttpService): BaseViewModel(service) {
    private val _allBanks = MutableLiveData<Optional<List<Bank>>>()
    val allBanks: LiveData<Optional<List<Bank>>>
                        get() = _allBanks

    private val _beneficiary = MutableLiveData<Optional<NameEnquiryResponse>>()
    val beneficiary: LiveData<Optional<NameEnquiryResponse>>
                        get() = _beneficiary


    var selectedBank = BankModel("", "", "")
    var accountNumber = ""

    val payment = MutableLiveData<Optional<PaymentModel>>()


    fun loadBanks() {
        uiScope.launch {
            // fetch banks on IO thread
            val banks = uiScope.async(Dispatchers.IO) { paymentService.getBanks() }
            // assign banks on main thread
            _allBanks.value = banks.await()
        }
    }

    fun validateBankDetails() {
        val requestHeader =  NameEnquiryRequestHeaderModel()
            requestHeader.authorisation = "InterswitchAuth SUtJQTkzODZEREFFMUYyQjExMkNFMjM2Q0FBNDcyQTgwQTkwRjk5QjM5ODc="
            requestHeader.clientId = "IKIA9386DDAE1F2B112CE236CAA472A80A90F99B3987"
            requestHeader.clientSecret = "E5jlYmDMw3nsPiNMI1Ys8fpmmHa6YRPEu675q6b6iFs"

        uiScope.launch {
            val beneficiary =  uiScope.async(Dispatchers.IO) { paymentService.nameEnquiry(requestHeader, selectedBank.selBankCodes.toString(), accountNumber)  }
            _beneficiary.value = beneficiary.await()
        }
    }
}