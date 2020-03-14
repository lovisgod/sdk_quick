package com.interswitchng.smartpos.shared.models.printer.info

enum class TransactionType {
    // NOTE: Do not change the order of these values, because of DB queries

    Purchase, Pre_Authorization, Completion, Refund, Reversal, CardNotPresent, BillPayment
}