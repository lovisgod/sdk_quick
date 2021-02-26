package com.interswitchng.smartpos.modules.main.transfer.models

import com.google.gson.annotations.SerializedName

data class NameEnquiryResponse(
        @SerializedName("accountName") val accountName: String
        )

