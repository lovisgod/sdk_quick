package com.interswitchng.interswitchpossdk.services

import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.services.PayableService
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleResponseHandler
import com.nhaarman.mockitokotlin2.*
import org.junit.Test

class PayableServiceTests {

    @Test
    fun `should call callback function with error`() {
        val callback: SimpleResponseHandler<List<Bank>?> = mock()

        val response: Simple<List<Bank>?> = mock()
        whenever(response.process(any())).then {
            val argumentCallback:SimpleResponseHandler<List<Bank>?> = it.getArgument(0)
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