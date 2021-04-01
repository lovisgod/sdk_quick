package com.interswitchng.smartpos.modules.main.billPayment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.modules.main.billPayment.models.BillDisplayDataModel
import com.interswitchng.smartpos.modules.main.billPayment.models.BillPaymentCategoriesModel
import com.interswitchng.smartpos.modules.main.billPayment.models.ChoosePackageConfigModel
import com.interswitchng.smartpos.modules.main.billPayment.repository.billRepo
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel

class BillPaymentViewmodel(val billRepo: billRepo): RootViewModel() {

    fun getCableBillers(): LiveData<ArrayList<BillDisplayDataModel>> {
        val resp = billRepo.getCableTvProviders()
        return MutableLiveData(resp)
    }

    fun getUtilityBillers(): LiveData<ArrayList<BillDisplayDataModel>> {
        val resp = billRepo.getUtilityBillers()
        return MutableLiveData(resp)
    }

    fun getDstvPackages(provider: String): MutableLiveData<ArrayList<BillPaymentCategoriesModel>> {
        val resp = billRepo.getDstvPackages(provider)
        return MutableLiveData(resp)
    }

    fun getBillerConfig(biller: BillDisplayDataModel): ChoosePackageConfigModel {
        val config = ChoosePackageConfigModel("")
        config.billerName = biller.title
        if (arrayListOf<String>("eko", "ibadan", "ikedc").contains(biller.title.toLowerCase())) {
            config.accountNumberField?.title = "Meter Number"
            config.phoneNumberField?.show = true
            config.phoneNumberField?.required = true
        }
            return config
        }
    }
