package com.interswitchng.interswitchpossdk.shared.services

import com.interswitchng.interswitchpossdk.shared.interfaces.IKeyValueStore

internal class KeyValueStore(private val prefManager: SharePreferenceManager): IKeyValueStore {

    override fun saveString(key: String, value: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getString(key: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveNumber(key: String, value: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNumber(key: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}