package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionResponseModel (
    var transactionResult: TransactionResultModel? =  null,
    var transactionType: PaymentModel.TransactionType = PaymentModel.TransactionType.CARD_PURCHASE
): Parcelable
