package com.interswitchng.smartpos.shared.services.kimono.models

// result generated from /xml

data class AllTerminalInfo(
        val terminalAllowedTxTypes: List<TerminalAllowedTxTypes>? = null,
        val terminalInfoBySerials: TerminalInfoBySerials? = null,
        val responseMessage: String = "",
        val responseCode: String = "")

data class TerminalAllowedTxTypes(val applicationDescription: String = "")

data class TerminalInfoBySerials(
        val cardAcceptorId: String = "",
        val merchantPhoneNumber: String = "",
        val merchantCity: String = "",
        val merchantEmail: String = "",
        val stateCodeAlphaTwo: String = "",
        val countryCodeAlphaTwo: String = "",
        val merchantState: String = "",
        val merchantName: String = "",
        val cardAcceptorNameLocation: String = "",
        val merchantAddress1: String = "",
        val merchantAddress2: String = "",
        val merchantId: String = "",
        val merchantCategoryCode: String = "",
        val terminalCode: String = "")
