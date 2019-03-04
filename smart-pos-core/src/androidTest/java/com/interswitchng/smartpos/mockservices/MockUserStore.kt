package com.interswitchng.smartpos.mockservices

import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.UserStore

internal class MockUserStore(private val keyValueStore: KeyValueStore): UserStore {
    override fun <T> getToken(callback: (String) -> T): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}