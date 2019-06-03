package com.interswitchng.smartpos.shared.models.core

import com.google.gson.Gson
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore


/**
 * A data model representing the terminal information downloaded from the server, which
 * is then used to configure the any given POS terminal
 *
 * - terminalId: the specific id of a terminal in a merchant's store
 * - merchantId: the merchant's id
 * - countryCode: a code identifying the country that the terminal is processing transactions in
 * - currencyCode: a code indicating the currency of the country
 * - callHomeTimeInMin: an integer indicating the time (in minutes) required to continuously call home
 * - serverTimeoutInSec: an integer indicating the time (in seconds) to specify as a connection timeout
 */
data class TerminalInfo(
        val terminalId: String,
        val merchantId: String,
        val merchantNameAndLocation: String,
        val merchantCategoryCode: String,
        val countryCode: String,
        val currencyCode: String,
        val callHomeTimeInMin: Int,
        val serverTimeoutInSec: Int) {


    internal fun persist(store: KeyValueStore) {
        val jsonString = Gson().toJson(this)
        store.saveString(PERSIST_KEY, jsonString)
    }

    override fun toString(): String {
        return """
            | terminalId: $terminalId
            | merchantId: $merchantId
            | merchantNameAndLocation: $merchantNameAndLocation
            | merchantCategory: $merchantCategoryCode
            | currencyCode: $currencyCode
            | callHomeTimeInMin: $callHomeTimeInMin
            | serverTimeoutInSec: $serverTimeoutInSec
        """.trimIndent()
    }


    companion object {

        private const val PERSIST_KEY = "terminal_data"

        internal fun get(store: KeyValueStore): TerminalInfo? {
            val jsonString = store.getString(PERSIST_KEY, "")
            return when(jsonString) {
                "" -> null
                else -> Gson().fromJson(jsonString, TerminalInfo::class.java)
            }
        }
    }
}

