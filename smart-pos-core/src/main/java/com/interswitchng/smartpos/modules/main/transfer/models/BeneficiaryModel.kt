package com.interswitchng.smartpos.modules.main.transfer.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeneficiaryModel(
        val accountNumber: String,
        val accountName: String
): Parcelable
