package com.interswitchng.interswitchpossdk.base

import com.interswitchng.interswitchpossdk.mockservices.MockKeyValueStore
import com.interswitchng.interswitchpossdk.mockservices.MockPOSDevice
import com.interswitchng.interswitchpossdk.mockservices.MockPayableService
import com.interswitchng.interswitchpossdk.mockservices.MockUserService
import com.interswitchng.interswitchpossdk.shared.interfaces.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.interfaces.IUserService
import com.interswitchng.interswitchpossdk.shared.interfaces.POSDevice
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import org.koin.dsl.module.module


private val mockServiceModule = module(override = true) {
    single<IKeyValueStore> { MockKeyValueStore() }
    single<Payable> { MockPayableService.Builder().build() }
    single<IUserService> { MockUserService(get()) }
    single<POSDevice> { MockPOSDevice() }
}


val mockAppModules = listOf(mockServiceModule)