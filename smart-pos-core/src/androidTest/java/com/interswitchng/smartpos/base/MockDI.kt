package com.interswitchng.smartpos.base

import com.interswitchng.smartpos.mockservices.MockKeyValueStore
import com.interswitchng.smartpos.mockservices.MockPayableService
import com.interswitchng.smartpos.mockservices.MockUserService
import com.interswitchng.smartpos.shared.interfaces.library.IKeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.IUserService
import com.interswitchng.smartpos.shared.interfaces.library.Payable
import org.koin.dsl.module.module


private val mockServiceModule = module(override = true) {
    single<IKeyValueStore> { MockKeyValueStore() }
    single<Payable> { MockPayableService.Builder().build() }
    single<IUserService> { MockUserService(get()) }
}


val mockAppModules = listOf(mockServiceModule)