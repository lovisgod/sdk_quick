package com.interswitchng.smartpos.shared.services.kimono.models


data class AgentIdResponse(
        var `$id`: String? = null,
        var address: String? = null,
        var merchantName: String? = null,
        var phoneNumber: String? = null,
        var responseCode: String? = null,
        var responseMessage: String? = null,
        var terminalId: String? = null
)
