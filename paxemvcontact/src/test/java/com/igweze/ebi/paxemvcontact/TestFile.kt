package com.igweze.ebi.paxemvcontact

import com.igweze.ebi.paxemvcontact.utilities.EmvUtils
import com.igweze.ebi.paxemvcontact.utilities.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class TestFile {


    @Test
    fun showTest() {

        val m = "DDDDFDDDDFMMMM"
        val result  = m.split("F")

        assertEquals(result.size, 3)
    }


    @Test
    fun hexToByte() {

        val string = "Some partition string"
        val bytes = string.toByteArray()
        println("byte length - ${bytes.size}")

        val hex = EmvUtils.bcd2Str(bytes)
        print("$hex ")
        println("hex length - ${hex.length}")
    }


    @Test
    fun format() {
        assertEquals(StringUtils.center(null, 0),null)
        assertEquals(StringUtils.center("foo", 3), ("foo"))
        assertEquals(StringUtils.center("foo", -1), ("foo"))
        assertEquals(StringUtils.center("moon", 10), ("   moon   "))
    }
}