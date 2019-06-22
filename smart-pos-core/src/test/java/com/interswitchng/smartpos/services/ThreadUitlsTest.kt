package com.interswitchng.smartpos.services

import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.utils.IswCompositeDisposable
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ThreadUitlsTest {

    @Test
    fun `should kill all threads when disposable is called`() {

        val composite = IswCompositeDisposable()

        val currentCount = Thread.activeCount()

        composite.add(ThreadUtils.createExecutor {
            val someCondition = true
            while (someCondition && !it.isDisposed) {
                Thread.sleep(300)
                println("Some message from the while loop: 1")
            }
        })

        composite.add(ThreadUtils.createExecutor {
            val someCondition = true
            while (someCondition && !it.isDisposed) {
                Thread.sleep(300)
                println("Some message from the while loop: 2")
            }
        })

        composite.add(ThreadUtils.createExecutor {
            val someCondition = true
            while (someCondition && !it.isDisposed) {
                Thread.sleep(300)
                println("Some message from the while loop: 3")
            }
        })

        assertEquals(currentCount + 3, Thread.activeCount())

        println("All threads setup")
        Thread.sleep(1000)

        composite.dispose()

        Thread.sleep(300)
        assertEquals(currentCount, Thread.activeCount())
    }

    @Test
    fun `should kill all threads even when threads are complete disposable is called`() {

        val composite = IswCompositeDisposable()

        val currentCount = Thread.activeCount()

        composite.add(ThreadUtils.createExecutor {
            Thread.sleep(500)
            println("Some message from the while loop: 1")
        })

        composite.add(ThreadUtils.createExecutor {
            val someCondition = true
            while (someCondition && !it.isDisposed) {
                Thread.sleep(300)
                println("Some message from the while loop: 2")
            }
        })

        composite.add(ThreadUtils.createExecutor {
            val someCondition = true
            while (someCondition && !it.isDisposed) {
                Thread.sleep(300)
                println("Some message from the while loop: 3")
            }
        })

        assertEquals(currentCount + 3, Thread.activeCount())

        println("All threads setup")
        Thread.sleep(1000)

        composite.dispose()

        Thread.sleep(300)
        assertEquals(currentCount, Thread.activeCount())
    }

    @Test
    fun test() {
        val m = PrintM
    }
}