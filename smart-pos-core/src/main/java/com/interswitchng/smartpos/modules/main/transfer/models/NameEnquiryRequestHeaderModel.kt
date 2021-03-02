package com.interswitchng.smartpos.modules.main.transfer.models


data class NameEnquiryRequestHeaderModel(
        var authorisation: String? = "",
        var clientSecret: String? = "",
        var clientId: String? = "",
        var signature: String? = "",
        var signatureMethod: String? = "",
        var timeStamp: String? = "",
        var nonce: String? = "",
        var host: String? = ""
)
