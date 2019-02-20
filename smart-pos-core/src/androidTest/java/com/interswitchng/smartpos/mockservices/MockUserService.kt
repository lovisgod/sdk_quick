package com.interswitchng.smartpos.mockservices

import com.interswitchng.smartpos.shared.interfaces.library.IKeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.IUserService

internal class MockUserService(private val keyValueStore: IKeyValueStore): IUserService {

    override fun getToken(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}