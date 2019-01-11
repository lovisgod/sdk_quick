package com.interswitchng.interswitchpossdk.utils

import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.IdlingResource

object WaitUtils {

    private val DEFAULT_WAIT_TIME = 3000

    private var idlingResource: IdlingResource? = null

    private val idlingRegistry = IdlingRegistry.getInstance()

    @JvmOverloads
    fun waitTime(waitingTime: Int = DEFAULT_WAIT_TIME) {
        cleanupWaitTime()

        idlingResource = ElapsedTimeIdlingResource(waitingTime.toLong())
        idlingResource?.let { idlingRegistry.register(it) }
    }

    fun cleanupWaitTime() {
        idlingResource?.let { idlingRegistry.unregister(it) }
        idlingResource = null
    }
}