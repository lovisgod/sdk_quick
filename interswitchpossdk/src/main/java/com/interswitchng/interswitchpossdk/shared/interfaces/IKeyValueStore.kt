package com.interswitchng.interswitchpossdk.shared.interfaces

internal interface IKeyValueStore {

    fun saveString(key: String, value: String)

    fun getString(key: String, default: String): String

    fun saveNumber(key: String, value: Long)

    fun getNumber(key: String, default: Long): Long

}