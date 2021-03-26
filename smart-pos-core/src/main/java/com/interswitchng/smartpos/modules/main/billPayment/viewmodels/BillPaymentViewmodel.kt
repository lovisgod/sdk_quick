package com.interswitchng.smartpos.modules.main.billPayment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.modules.main.billPayment.models.BillDisplayDataModel
import com.interswitchng.smartpos.modules.main.billPayment.repository.billRepo
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel

class BillPaymentViewmodel(val billRepo: billRepo): RootViewModel() {

    fun getCableBillers(): LiveData<ArrayList<BillDisplayDataModel>> {
        val resp = billRepo.getCableTvProviders()
        return MutableLiveData(resp)
    }
}