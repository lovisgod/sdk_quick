package com.interswitchng.interswitchpossdk.base

import com.interswitchng.interswitchpossdk.mockservices.MockKeyValueStore
import com.interswitchng.interswitchpossdk.mockservices.MockPayableService
import com.interswitchng.interswitchpossdk.mockservices.MockUserService
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IUserService
import com.interswitchng.interswitchpossdk.shared.interfaces.library.Payable
import org.koin.dsl.module.module


private val mockServiceModule = module(override = true) {
    single<IKeyValueStore> { MockKeyValueStore() }
    single<Payable> { MockPayableService.Builder().build() }
    single<IUserService> { MockUserService(get()) }
}


val mockAppModules = listOf(mockServiceModule)