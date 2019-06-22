package com.interswitchng.smartpos.shared.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class TransactionResultViewModel(private val posDevice: POSDevice): RootViewModel() {

    private val _printButton = MutableLiveData<Boolean>()
    val printButton: LiveData<Boolean> get() = _printButton

    private val _printerMessage = MutableLiveData<String>()
    val printerMessage: LiveData<String> get() = _printerMessage

    private val _printedCustomerCopy = MutableLiveData<Boolean>()
    val printedCustomerCopy: LiveData<Boolean> get() = _printedCustomerCopy

    private val _printedMerchantCopy = MutableLiveData<Boolean>()
    val printedMerchantCopy: LiveData<Boolean> get() = _printedMerchantCopy



    fun printSlip(user: UserType, slip: TransactionSlip, reprint: Boolean = false) {

        uiScope.launch {
            // get printer status on IO thread
            val printStatus = withContext(ioScope) { posDevice.printer.canPrint() }

            when (printStatus) {
                is PrintStatus.Error -> _printerMessage.value = printStatus.message
                else -> {
                    // disable print button
                    _printButton.value = false

                    // get items to print
                    val slipItems = slip.getSlipItems()

                    // add re-print flag
                    if (reprint) {
                        val rePrintFlag = PrintObject.Data("*** Re-Print ***", PrintStringConfiguration(displayCenter = true, isBold = true))
                        slipItems += rePrintFlag
                    }

                    // print code in IO thread
                    val status = withContext(ioScope) { posDevice.printer.printSlip(slipItems, user) }
                    // publish print message
                    _printerMessage.value = status.message
                    // enable print button
                    _printButton.value = true

                    // set printed flags for customer and merchant copies
                    val printed = status !is PrintStatus.Error
                    _printedCustomerCopy.value = printed && user == UserType.Customer
                    _printedMerchantCopy.value = printed && user == UserType.Merchant
                }
            }
        }
    }

}