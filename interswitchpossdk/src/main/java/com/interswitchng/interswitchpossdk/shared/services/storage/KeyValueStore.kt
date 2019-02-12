package com.interswitchng.interswitchpossdk.shared.services.storage

import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.utilities.Logger

internal class KeyValueStore(private val prefManager: SharePreferenceManager): IKeyValueStore {

    val logger = Logger.with("KeyValueStore")

    override fun saveString(key: String, value: String) {
        val result = prefManager.saveString(key, value)
        logger.log("saveString: $result")
    }

    override fun getString(key: String, default: String): String {
        return prefManager.getString(key, default) ?: ""
    }

    override fun saveNumber(key: String, value: Long) {
        prefManager.saveNumber(key, value)
    }

    override fun getNumber(key: String, default: Long): Long {
        return prefManager.getNumber(key, default)
    }

    companion object {

    }

}