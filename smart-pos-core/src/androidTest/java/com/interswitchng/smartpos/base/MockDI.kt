package com.interswitchng.smartpos.base

import com.interswitchng.smartpos.mockservices.MockKeyValueStore
import com.interswitchng.smartpos.mockservices.MockHttpServiceService
import com.interswitchng.smartpos.mockservices.MockUserStore
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.UserStore
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import org.koin.dsl.module.module


private val mockServiceModule = module(override = true) {
    single<KeyValueStore> { MockKeyValueStore() }
    single<HttpService> { MockHttpServiceService.Builder().build() }
    single<UserStore> { MockUserStore(get()) }
}


val mockAppModules = listOf(mockServiceModule)