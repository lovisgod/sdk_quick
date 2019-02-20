package com.interswitchng.interswitchpossdk.services

import com.interswitchng.interswitchpossdk.shared.interfaces.network.IHttpService
import com.interswitchng.interswitchpossdk.shared.interfaces.network.TransactionRequeryCallback
import com.interswitchng.interswitchpossdk.shared.models.transaction.PaymentType
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.interswitchpossdk.shared.services.PayableService
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleResponseHandler
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.Executors

class PayableServiceTests {


    @Test
    fun `should invoke 'onTransactionComplete' of callback when the transactionStatus is completed`() {

        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "")


        val successTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn true
            on(mock.isPending()) doReturn false
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.test(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(successTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getQrTransactionStatus(any(), any())) doReturn simpleResponse
        }

        val threadCountBeforeServiceCall = Thread.activeCount()
        val service = PayableService(httpService)
        service.checkPayment(PaymentType.QR, status, 3000, transactionStatusCallback)
        val threadCountDuringServiceCall = Thread.activeCount()
        assertEquals(threadCountBeforeServiceCall + 1, threadCountDuringServiceCall)


        Thread.sleep(3000)
        verify(transactionStatusCallback, times(1)).onTransactionCompleted(successTransaction)
        verify(transactionStatusCallback, times(0)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(0)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
        assertTrue(threadCountDuringServiceCall > Thread.activeCount())
    }


    @Test
    fun `should invoke 'onTransactionStillPending' of callback when the transactionStatus is pending`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "")


        val pendingTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn false
            on(mock.isPending()) doReturn true
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.test(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(pendingTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getQrTransactionStatus(any(), any())) doReturn simpleResponse
        }


        val service = PayableService(httpService)
        val threadCountBeforeServiceCall = Thread.activeCount()
        service.checkPayment(PaymentType.QR, status, 3000, transactionStatusCallback)
        val threadCountDuringServiceCall = Thread.activeCount()
        assertEquals(threadCountBeforeServiceCall + 1, threadCountDuringServiceCall)


        Thread.sleep(2800)
        assertTrue(threadCountDuringServiceCall == Thread.activeCount())
        verify(transactionStatusCallback, times(0)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(1)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(0)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
    }

    @Test
    fun `should invoke 'onTransactionTimeout' of callback when the transactionStatus is pending beyond specified timeout`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "")


        val pendingTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn false
            on(mock.isPending()) doReturn true
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.test(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(pendingTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getUssdTransactionStatus(any(), any())) doReturn simpleResponse
        }

        val service = PayableService(httpService)
        val threadCountBeforeServiceCall = Thread.activeCount()
        service.checkPayment(PaymentType.USSD, status, 6000, transactionStatusCallback)


        Thread.sleep(7500)
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
        verify(transactionStatusCallback, times(2)).onTransactionStillPending(any())
        val threadCountDuringServiceCall = Thread.activeCount()
        assertEquals(threadCountBeforeServiceCall + 1, threadCountDuringServiceCall)


        Thread.sleep(1000)
        assertTrue(threadCountDuringServiceCall > Thread.activeCount())
        verify(transactionStatusCallback, times(0)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(0)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(1)).onTransactionTimeOut()
    }



    @Test
    fun `should invoke 'onTransactionError' of callback when the transactionStatus is not pending or completed`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "")


        val pendingTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn false
            on(mock.isPending()) doReturn false
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.test(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(pendingTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getUssdTransactionStatus(any(), any())) doReturn simpleResponse
        }


        val service = PayableService(httpService)
        val threadCountBeforeServiceCall = Thread.activeCount()
        service.checkPayment(PaymentType.USSD, status, 3000, transactionStatusCallback)
        val threadCountDuringServiceCall = Thread.activeCount()
        assertEquals(threadCountBeforeServiceCall + 1, threadCountDuringServiceCall)


        Thread.sleep(2000)
        assertTrue(threadCountDuringServiceCall > Thread.activeCount())
        verify(transactionStatusCallback, times(0)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(0)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(1)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
    }


    @Test
    fun `should invoke callback function with response`() {
        val callback: SimpleResponseHandler<List<Bank>?> = mock()

        val response: Simple<List<Bank>?> = mock()
        whenever(response.process( any())).then {
            val argumentCallback:  SimpleResponseHandler<List<Bank>?> = it.getArgument(0)
            argumentCallback(listOf(), null)
        }

        val httpService: IHttpService = mock()
        whenever(httpService.getBanks()).doReturn(response)

        val service = PayableService(httpService)
        service.getBanks(callback)


        verify(httpService, times(1)).getBanks()
        verify(callback, times(1)).invoke(any(), anyOrNull())
    }

    @Test
    fun testExecutor() {
        val scheduler = Executors.newSingleThreadScheduledExecutor()

        println(Thread.activeCount())
        scheduler.execute {
            println("before sleep")
            Thread.sleep(5000)
            println("after sleep")
        }

        Thread.sleep(3000)
        scheduler.shutdownNow()
        Thread.sleep(3000)

        assertEquals(1, Thread.activeCount())
    }


    @Test
    fun testCopy() {
        val bytes = byteArrayOf(1,2,3,4,5,6)
        val copy = ByteArray(bytes.size - 4)
        if (bytes.size > 4)
            System.arraycopy(bytes, 0, copy, 0, bytes.size - 4)

        for (b in copy)
            print(b)

        println(copy.size)

        val m = String(byteArrayOf(0x0))
        println(m.length)
        println(m.isEmpty())
        println(m.isBlank())
    }
}