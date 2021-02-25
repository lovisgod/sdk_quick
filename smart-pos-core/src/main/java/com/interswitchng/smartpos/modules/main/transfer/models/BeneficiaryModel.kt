package com.interswitchng.smartpos.modules.main.transfer.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeneficiaryModel(
        val accountNumber: Int,
        val accountName: String
): Parcelable
