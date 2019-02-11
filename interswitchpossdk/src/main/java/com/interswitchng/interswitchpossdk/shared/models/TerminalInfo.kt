package com.interswitchng.interswitchpossdk.shared.models

import com.google.gson.Gson
import com.interswitchng.interswitchpossdk.shared.interfaces.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.services.iso8583.TerminalData

data class TerminalInfo(
    val terminalId: String,
    val merchantId: String,
    val merchantLocation: String,
    val merchantCategoryCode: String,
    val countryCode: String,
    val currencyCode: String,
    val callHomeTimeInMin: Int,
    val serverTimeoutInSec: Int) {


    fun persist(store: IKeyValueStore) {
        val jsonString = Gson().toJson(this)
        store.saveString(PERSIST_KEY, jsonString)
    }

    override fun toString(): String {
        return """
            | terminalId: $terminalId
            | merchantId: $merchantId
            | merchantLocation: $merchantLocation
            | merchantCategory: $merchantCategoryCode
            | currencyCode: $currencyCode
            | callHomeTimeInMin: $callHomeTimeInMin
            | serverTimeoutInSec: $serverTimeoutInSec
        """.trimIndent()
    }


    companion object {

        private const val PERSIST_KEY = "terminal_data"

        internal fun get(store: IKeyValueStore): TerminalData? {
            val jsonString = store.getString(PERSIST_KEY, "")
            return when(jsonString) {
                "" -> null
                else -> Gson().fromJson(jsonString, TerminalData::class.java)
            }
        }
    }
}

