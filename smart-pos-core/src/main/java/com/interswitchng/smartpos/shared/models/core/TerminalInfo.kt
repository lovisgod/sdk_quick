package com.interswitchng.smartpos.shared.models.core

import android.app.Notification
import com.google.gson.Gson
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.shared.Constants
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
        val serverTimeoutInSec: Int,
        var isKimono: Boolean = false,
        var isKimono3: Boolean = false,
        val capabilities: String? = null,
        var serverUrl: String = Constants.ISW_KIMONO_BASE_URL,
        var serverIp: String = Constants.ISW_TERMINAL_IP,
        var serverPort: Int = BuildConfig.ISW_TERMINAL_PORT,
        val agentId: String,
        val agentEmail: String) {


    internal fun persist(store: KeyValueStore): Boolean {
//        val jsonString = Gson().toJson(this)
//        store.saveString(PERSIST_KEY, jsonString)


        if (serverUrl.isNullOrEmpty()) serverUrl = Constants.ISW_KIMONO_BASE_URL
        if (serverIp.isNullOrEmpty()) serverIp = Constants.ISW_TERMINAL_IP
        if (serverPort == 0) serverPort = BuildConfig.ISW_TERMINAL_PORT


        // get previous terminal info
        val prevInfo = get(store)

        // save only when config changed
        if (prevInfo != this) {
            val jsonString = Gson().toJson(this)
            store.saveString(PERSIST_KEY, jsonString)
            return true
        }

        return false
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
                else -> {
                   val data= Gson().fromJson(jsonString, TerminalInfo::class.java)
//
                    data
                }
                }
            }




        internal fun getSettingsSettlementChoice(store: KeyValueStore): Boolean {

            var selectedSettlement =store.getString(Constants.SETTINGS_TERMINAL_CONFIG_TYPE,"kimono")
            if (selectedSettlement=="kimono"){
               return  true
            }

            return  false


        }


        internal fun setSettingsSettlementChoice(isKimono: Boolean,store: KeyValueStore) {

            if(isKimono)
            store.saveString(Constants.SETTINGS_TERMINAL_CONFIG_TYPE,"kimono")
            else
                store.saveString(Constants.SETTINGS_TERMINAL_CONFIG_TYPE,"nibss")


        }


    }
}

