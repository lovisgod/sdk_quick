package com.interswitchng.smartpos.shared.models.transaction

import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse

enum class CardOnlineProcessResult {
    NO_EMV,
    NO_RESPONSE,
    ONLINE_DENIED,
    ONLINE_APPROVED
}

data class CardReadTransactionResponse(
        var onlineProcessResult: CardOnlineProcessResult? = null,
        var transactionResponse: TransactionResponse? = null,
        var emvData: EmvData? = null
)