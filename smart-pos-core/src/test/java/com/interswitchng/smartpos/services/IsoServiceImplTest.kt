package com.interswitchng.smartpos.services

import org.junit.Assert.assertEquals
import org.junit.Test

class IsoServiceImplTest {

    private fun getPan(code: String): String {

        val bin = "506101"
        var binAndCode = "$bin$code"
        val remainder = 12 - code.length
        // pad if less than 12
        if (remainder > 0)
            binAndCode = "$binAndCode${"0".repeat(remainder)}"

        var nSum = 0
        var alternate = true
        for (i in binAndCode.length - 1 downTo 0) {

            var d = binAndCode[i] - '0'

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

        return "$binAndCode$checkDigit"
    }

    @Test
    fun should_return_correct_card_check_digits() {
        val code = "123456789"
        val expectedPan = "5061011234567890008"

        val actualPan = getPan(code)
        assertEquals(expectedPan, actualPan)
    }
}