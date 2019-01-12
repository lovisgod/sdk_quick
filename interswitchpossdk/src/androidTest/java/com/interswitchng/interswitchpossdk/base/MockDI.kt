package com.interswitchng.interswitchpossdk.base

import com.interswitchng.interswitchpossdk.mockservices.MockKeyValueStore
import com.interswitchng.interswitchpossdk.mockservices.MockPayableService
import com.interswitchng.interswitchpossdk.mockservices.MockUserService
import org.koin.dsl.module.module


private val mockServiceModule = module(override = true) {
    single { MockKeyValueStore() }
    single { MockPayableService() }
    single { MockUserService(get()) }
}


val mockAppModules = listOf(mockServiceModule)