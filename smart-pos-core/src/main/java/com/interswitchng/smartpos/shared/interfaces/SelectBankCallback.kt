package com.interswitchng.smartpos.shared.interfaces

import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank

internal interface SelectBankCallback {
    fun onBankSelected(bank: Bank)

    fun loadBanks(callback: (List<Bank>) -> Unit)
}