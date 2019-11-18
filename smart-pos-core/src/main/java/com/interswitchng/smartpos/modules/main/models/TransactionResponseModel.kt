package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionResponseModel (
    var transactionResult: TransactionResult? =  null,
    var transactionType: PaymentModel.TransactionType = PaymentModel.TransactionType.CARD_PURCHASE
): Parcelable
