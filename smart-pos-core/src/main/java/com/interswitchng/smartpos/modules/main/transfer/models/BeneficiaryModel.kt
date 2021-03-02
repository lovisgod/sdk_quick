package com.interswitchng.smartpos.modules.main.transfer.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeneficiaryModel(
        val accountCurrency: String? = "",
        val accountName: String? = "",
        val accountNumber: String? = "",
        val accountType: String? = "",
        val addrLine1: String? = "",
        val addrLine2: String? = "",
        val city: String? = "",
        val countryCode: String? = "",
        val countryOfBirth: String? = "",
        val countryOfIssue: String? = "",
        val dob: String? = "",
        val expiryDate: String? = "",
        val firstName: String? = "",
        val idNumber: String? = "",
        val idType: String? = "",
        val lastName: String? = "",
        val nationality: String? = "",
        val otherNames: String?= "",
        val phone: String? = "",
        val postalCode: String?= "",
        val stateCode: String? = ""
): Parcelable
