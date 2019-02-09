package com.interswitchng.interswitchpossdk.shared.services.storage

import com.interswitchng.interswitchpossdk.shared.interfaces.IKeyValueStore

internal class KeyValueStore(private val prefManager: SharePreferenceManager): IKeyValueStore {

    override fun saveString(key: String, value: String) {

    }

    override fun getString(key: String, default: String): String {
        return ""
    }

    override fun saveNumber(key: String, value: Long) {

    }

    override fun getNumber(key: String, default: Long): Long {
        return 0
    }

}