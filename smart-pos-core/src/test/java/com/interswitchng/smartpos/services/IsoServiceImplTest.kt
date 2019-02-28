package com.interswitchng.smartpos.services

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsoServiceImplTest {

    private fun getBinAndCode(code: String): String {

        val bin = "506101"
        var binAndCode = "$bin$code"
        val remainder = 12 - code.length
        // pad if less than 12
        if (remainder > 0)
            binAndCode = "$binAndCode${"0".repeat(remainder)}"

        return binAndCode
    }

    private fun getPan(code: String): String {

        var nSum = 0
        var alternate = true
        for (i in code.length - 1 downTo 0) {

            var d = code[i] - '0'

            if (alternate)
                d *= 2

            // We add two digits to handle
            // cases that make two digits after
            // doubling
            nSum += d / 10
            nSum += d % 10

            alternate = !alternate
        }

        val unitDigit = nSum % 10
        val checkDigit = 10 - unitDigit

        return "$code$checkDigit"
    }

    @Test
    fun should_return_correct_card_check_digits() {
        val code = getBinAndCode("123456789")
        val expectedPan = "5061011234567890008"

        val actualPan = getPan(code)
        assertEquals(expectedPan, actualPan)

        val code2 = "422584110620516"
        val expectedPan2 = "${code2}2"
        val actualPan2 = getPan(code2)


        assertTrue(check(expectedPan2))

        assertEquals(expectedPan2, actualPan2)

    }

    fun check(ccNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in ccNumber.length - 1 downTo 0) {
            var n = Integer.parseInt(ccNumber.substring(i, i + 1))
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = n % 10 + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}