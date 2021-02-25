package com.interswitchng.smartpos.modules.main.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gojuno.koptional.Optional
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.BaseViewModel
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class TransferViewModel(service: HttpService): BaseViewModel(service) {
    private val _allBanks = MutableLiveData<Optional<List<Bank>>>()
    val allBanks: LiveData<Optional<List<Bank>>> get() = _allBanks

    val selectedBank = MutableLiveData<Optional<BankModel>>()
    val selectedBeneficiary = MutableLiveData<Optional<BeneficiaryModel>>()
    val payment = MutableLiveData<Optional<PaymentModel>>()


    fun loadBanks() {
        uiScope.launch {
            // fetch banks on IO thread
            val banks = uiScope.async(Dispatchers.IO) { paymentService.getBanks() }
            // assign banks on main thread
            _allBanks.value = banks.await()
        }
    }
}