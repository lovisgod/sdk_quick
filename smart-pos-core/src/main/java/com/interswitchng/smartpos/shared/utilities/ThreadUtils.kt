package com.interswitchng.smartpos.shared.utilities

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.interswitchng.smartpos.shared.models.utils.IswDisposable

object ThreadUtils {

    fun createExecutor(task: (IswDisposable) -> Unit): IswDisposable {
        // create a disposable
        val disposable = IswDisposable()
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