package com.interswitchng.interswitchpossdk.shared.interfaces

internal interface IKeyValueStore {

    fun saveString(key: String, value: String)

    fun getString(key: String)

    fun saveNumber(key: String, value: Int)

    fun getNumber(key: String)

}