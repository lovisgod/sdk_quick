package com.interswitchng.smartpos.adapter

import com.interswitchng.smartpos.shared.utilities.Simple
import com.interswitchng.smartpos.shared.utilities.SimpleAdapterFactory
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Type

class SimpleAdapterFactoryTest {

    @Test
    fun test() {
        val factory = SimpleAdapterFactory.create()
        val expectedType = String::class.java
        val mockd: Array<String> = arrayOf()
        val type: Type = mockd.javaClass
        val callAdapter = factory.get(mockd as Type, null, null)

        val actualType = callAdapter?.responseType()
        assertEquals(expectedType, actualType)
    }
}