package com.interswitchng.smartpos.shared.utilities

import android.os.Handler
import android.os.Looper
import com.interswitchng.smartpos.shared.models.utils.Disposable

internal object ThreadUtils {

    fun createExecutor(task: (Disposable) -> Unit): Disposable {
        // create a disposable
        val disposable = Disposable()
        // create a thread
        val thread = Thread {
            // run task using disposable
            task(disposable)
        }

        val mainHandler = Handler(Looper.getMainLooper())
        // add on dispose listener
        disposable.onDispose {
            // end running thread
            mainHandler.post { thread.join() }
        }
        // start thread
        thread.start()

        // return disposable
        return disposable
    }

}