package com.interswitchng.smartpos.modules.main.transfer.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BankModel(
    val recvInstId: String,
    val selBankCodes: String?,
    val bankName: String
): Parcelable
