package com.interswitchng.interswitchpossdk.services

import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.interfaces.TransactionRequeryCallback
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.shared.services.PayableService
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleResponseHandler
import com.nhaarman.mockitokotlin2.*
import org.junit.Test

class PayableServiceTests {


    @Test
    fun `should invoke 'onTransactionComplete' of callback when the transactionStatus is completed`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "", "")


        val successTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn true
            on(mock.isPending()) doReturn false
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.process(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(successTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getTransactionStatus(any(), any(), any())) doReturn simpleResponse
        }

        val service = PayableService(httpService)
        service.checkPayment(status, 3000, transactionStatusCallback)
        Thread.sleep(2000)


        verify(transactionStatusCallback, times(1)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(0)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(0)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
    }


    @Test
    fun `should invoke 'onTransactionStillPending' of callback when the transactionStatus is pending`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "", "")


        val pendingTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn false
            on(mock.isPending()) doReturn true
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.process(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(pendingTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getTransactionStatus(any(), any(), any())) doReturn simpleResponse
        }

        val service = PayableService(httpService)
        service.checkPayment(status, 3000, transactionStatusCallback)
        Thread.sleep(2000)


        verify(transactionStatusCallback, times(0)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(1)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(0)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
    }

    @Test
    fun `should invoke 'onTransactionTimeout' of callback when the transactionStatus is pending beyond specified timeout`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "", "")


        val pendingTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn false
            on(mock.isPending()) doReturn true
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.process(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(pendingTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getTransactionStatus(any(), any(), any())) doReturn simpleResponse
        }

        val service = PayableService(httpService)
        service.checkPayment(status, 3000, transactionStatusCallback)
        Thread.sleep(6000)


        verify(transactionStatusCallback, times(0)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(0)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(0)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(1)).onTransactionTimeOut()
    }



    @Test
    fun `should invoke 'onTransactionError' of callback when the transactionStatus is pending`() {
        val transactionStatusCallback: TransactionRequeryCallback = mock()
        val status = TransactionStatus("", "", "")


        val pendingTransaction: Transaction = mock {
            on(mock.isCompleted()) doReturn false
            on(mock.isPending()) doReturn false
        }

        val simpleResponse: Simple<Transaction?> = mock {
            on(mock.process(any())).doAnswer {
                val argumentCallback:  SimpleResponseHandler<Transaction?> = it.getArgument(0)
                argumentCallback(pendingTransaction, null)
            }
        }

        val httpService: IHttpService = mock {
            on(mock.getTransactionStatus(any(), any(), any())) doReturn simpleResponse
        }

        val service = PayableService(httpService)
        service.checkPayment(status, 3000, transactionStatusCallback)
        Thread.sleep(2000)


        verify(transactionStatusCallback, times(0)).onTransactionCompleted(any())
        verify(transactionStatusCallback, times(0)).onTransactionStillPending(any())
        verify(transactionStatusCallback, times(1)).onTransactionError(anyOrNull(), anyOrNull())
        verify(transactionStatusCallback, times(0)).onTransactionTimeOut()
    }


    @Test
    fun `should invoke callback function with response`() {
        val callback: SimpleResponseHandler<List<Bank>?> = mock()

        val response: Simple<List<Bank>?> = mock()
        whenever(response.process(any())).then {
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


}