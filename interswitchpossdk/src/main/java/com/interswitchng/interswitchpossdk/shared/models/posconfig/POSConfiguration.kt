package com.interswitchng.interswitchpossdk.shared.models.posconfig

import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo

internal data class POSConfiguration(
        internal val alias: String,
        internal val terminalId: String,
        internal val merchantId: String,
        internal val terminalType: String,
        internal val uniqueId: String,
        internal val merchantLocation: String,
        internal val merchantCode: String,
        internal val merchantName: String = ""
) {


    companion object {
        private const val KEY_MERCHANT_NAME = "merchant_name"
        private const val KEY_MERCHANT_LOCATION = "merchant_location"

        internal fun default(): POSConfiguration {
            val empty = ""
            return POSConfiguration(empty, empty, empty, empty, empty, empty, empty)
        }

        internal fun get(store: IKeyValueStore): POSConfiguration? {
            return TerminalInfo.get(store)?.let {
                val alias = "000007"
                val terminalType = "PAX"
                val uniqueId = "280-820-589"
                val merchantCode = "MX5882"
                val merchantName = store.getString(KEY_MERCHANT_NAME, "")
                val merchantLocation = store.getString(KEY_MERCHANT_LOCATION, "")

                return@let POSConfiguration(alias, it.terminalId, it.merchantId, terminalType, uniqueId,
                        merchantLocation, merchantCode, merchantName)
            }
        }
    }
}
