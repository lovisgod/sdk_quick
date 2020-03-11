package com.interswitchng.smartpos.modules.menu.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.TransactionLogService
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ReportViewModel(
    private val posDevice: POSDevice,
    private val transactionLogService: TransactionLogService
) : RootViewModel() {


    private val _printButton = MutableLiveData<Boolean>()
    val printButton: LiveData<Boolean> get() = _printButton

    private val _printerMessage = MutableLiveData<String>()
    val printerMessage: LiveData<String> get() = _printerMessage

    private val _endOfDatTransactions = MutableLiveData<List<TransactionLog>>()
    val endOfDatTransactions: LiveData<List<TransactionLog>> = _endOfDatTransactions


    // setup paged list config
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(10)
        .setPageSize(10)
        .build()

    fun getReport(day: Date): LiveData<PagedList<TransactionLog>> {
        return transactionLogService.getTransactionFor(day, config)
    }


    fun printEndOfDay(transactions: List<TransactionLog>) {

        uiScope.launch {
            // get printer status on IO thread
            val printStatus = withContext(ioScope) {
                posDevice.printer.canPrint()
            }

            when (printStatus) {
                is PrintStatus.Error -> {
                    _printerMessage.value = printStatus.message
                }
                else -> {
                    // get slip for current date
                    val slipItems = transactions.toSlipItems()

                    // print code in IO thread
                    val status = withContext(ioScope) {
                        posDevice.printer.printSlip(
                            slipItems,
                            UserType.Merchant
                        )
                    }

                    // publish print message
                    _printerMessage.value = status.message
                    // enable print button
                    _printButton.value = true

                    // set printed flags for customer and merchant copies
                    val printed = status !is PrintStatus.Error
                }
            }
        }
    }

    fun getEndOfDay(date: Date): LiveData<List<TransactionLog>> {
        // disable print button
        _printButton.value = false

        return transactionLogService.getTransactionFor(date)
    }


    private fun List<TransactionLog>.toSlipItems(): MutableList<PrintObject> {
        // create the title for printout
        val title = PrintObject.Data("Purchase", PrintStringConfiguration(isTitle = true, displayCenter = true))

        // initialize list with the title and a line under
        val list = mutableListOf(title, PrintObject.Line)

        // add title

        // add each item into the end of day list
        this.forEach {
            val slipItem = it.toSlipItem()
            list.add(slipItem)
        }

        // add the summary here

        return list
    }

    private fun TransactionLog.toSlipItem(): PrintObject  {
        val date = Date(this.time)
        val dateStr = DateUtils.hourMinuteFormat.format(date)
        val amount = this.amount
        val code = this.responseCode
        val status = if (code == IsoUtils.OK) "PASS" else "FAIL"

        return PrintObject.Data("$dateStr - $amount - $code - $status")
    }
}