package com.igweze.ebi.paxemvcontact.emv

enum class ICCData(val tagName: String, val tag: Int) {
    // emv request and sensitive data
    AUTHORIZATION_REQUEST("Authorization Request", 0x9F26),
    CRYPTOGRAM_INFO_DATA("Cryptogram Information Data", 0x9F27),
    ISSUER_APP_DATA("Issuer Application Data", 0x9F10),
    UNPREDICTABLE_NUMBER("Unpredictable Number", 0x9F37),
    APPLICATION_TRANSACTION_COUNTER("App Transaction Counter", 0x9F36),
    TERMINAL_VERIFICATION_RESULT("Terminal Verification Result", 95),
    TRANSACTION_DATE("Transaction Date", 0x9A),
    TRANSACTION_TYPE("Transaction Type", 0x9C),
    TRANSACTION_AMOUNT("Transaction Amount", 0x9F02),
    TRANSACTION_CURRENCY_CODE("Currency Code", 0x5F2A),
    APPLICATION_INTERCHANGE_PROFILE("App Interchange Profile", 82),
    TERMINAL_COUNTRY_CODE("Terminal Country Code", 0x9F1A),
    CARD_HOLDER_VERIFICATION_RESULT("CVM Results", 0x9F34),
    TERMINAL_CAPABILITIES("Terminal Capabilities", 0x9F33),
    TERMINAL_TYPE("Terminal Type", 0x9F35),
    INTERFACE_DEVICE_SERIAL_NUMBER("IDSN", 0X9F1E),
    DEDICATED_FILE_NAME("Dedicated File Name", 84),
    APP_VERSION_NUMBER("App Version Number", 0x9F09),
    AMOUNT("Amount", 0x9F03),
    APP_PAN_SEQUENCE_NUMBER("App PAN Sequence Number", 0x5F34),


    // Issuer response
    ISSUER_AUTHENTICATION("Issuer Authentication", 91),
    ISSUER_SCRIPT1("Issuer script 1", 71),
    ISSUER_SCRIPT2("Issuer script 1", 72)

}

val REQUEST_TAGS = listOf(ICCData.AUTHORIZATION_REQUEST,
        ICCData.CRYPTOGRAM_INFO_DATA,
        ICCData.ISSUER_APP_DATA,
        ICCData.UNPREDICTABLE_NUMBER,
        ICCData.APPLICATION_TRANSACTION_COUNTER,
        ICCData.TERMINAL_VERIFICATION_RESULT,
        ICCData.TRANSACTION_DATE,
        ICCData.TRANSACTION_TYPE,
        ICCData.TRANSACTION_AMOUNT,
        ICCData.TRANSACTION_CURRENCY_CODE,
        ICCData.APPLICATION_INTERCHANGE_PROFILE,
        ICCData.TERMINAL_COUNTRY_CODE,
        ICCData.CARD_HOLDER_VERIFICATION_RESULT,
        ICCData.TERMINAL_CAPABILITIES,
        ICCData.TERMINAL_TYPE,
        ICCData.INTERFACE_DEVICE_SERIAL_NUMBER,
        ICCData.DEDICATED_FILE_NAME,
        ICCData.APP_VERSION_NUMBER,
        ICCData.AMOUNT,
        ICCData.APP_PAN_SEQUENCE_NUMBER)
