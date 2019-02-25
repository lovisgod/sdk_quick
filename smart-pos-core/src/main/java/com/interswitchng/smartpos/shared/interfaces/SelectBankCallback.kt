package com.interswitchng.smartpos.shared.interfaces

import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank

interface SelectBankCallback {
    fun onBankSelected(bank: Bank)
}