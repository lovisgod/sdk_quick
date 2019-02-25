package com.interswitchng.smartpos.shared.models.utils

import java.util.*

internal class CompositeDisposable {

    private val disposables = Stack<Disposable>()

    fun add(disposable: Disposable) {
        disposables.push(disposable)
    }

    fun dispose() {
        for (i in 0 until disposables.size) {
            val disposable = disposables.pop()
            disposable.dispose()
        }
    }
}