package com.interswitchng.smartpos.emv.pax

import com.interswitchng.smartpos.emv.pax.models.TerminalConfig
import com.interswitchng.smartpos.emv.pax.utilities.EmvUtils
import com.interswitchng.smartpos.emv.pax.utilities.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister

class TestFile {

    @Root(name = "sloths", strict = false)
    class SimpleClass {
        @field:Element(name = "x")
        var xAttribute: Int = 0
        @field:Element(name = "y")
        var yAttribute: Int = 0
    }

    @Test
    fun showTest() {

        val m = "A0000000041010"
        val sub = m.substring(0 until m.length-1)
        assertEquals(sub, "A000000004101")
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
        assertEquals(StringUtils.center(null, 0), null)
        assertEquals(StringUtils.center("foo", 3), ("foo"))
        assertEquals(StringUtils.center("foo", -1), ("foo"))
        assertEquals(StringUtils.center("moon", 10), ("   moon   "))
    }

    @Test
    fun shouldParseXMLcorrectly() {

        val expected = SimpleClass().apply { xAttribute = 1; yAttribute = 2}
        val actual = Persister().read(SimpleClass::class.java, "<Simple><x>1</x><y>2</y></Simple>")
        assertEquals(expected.xAttribute, actual.xAttribute)
        assertEquals(expected.yAttribute, actual.yAttribute)
    }

    @Test
    fun shouldReadEmvXmlFromFileCorrectly() {
        val xmlFile = Utilities.getStream("sample_aids.xml")
        val actual = EmvUtils.getAids(xmlFile)

        assertEquals(2, actual.cards.size)
        assertEquals(2, actual.cards[0].keys.size)
        assertEquals(3, actual.cards[1].keys.size)
    }

    @Test
    fun shouldReadTerminalConfigXmlFileCorrectly() {

        val terminalXml = Utilities.getStream("sample_terminal.xml")
        val expectedTerminal = TerminalConfig().apply {
            supportpse = true
            floorlimitcheck = true
            floorlimit = 100
            tacdenial = "0010000000"
            taconline = "FCF8E4F880"
            tacdefault = "FCF0E40800"
            ddol = "9F3704"
            tdol = "N/A"
            version = "0020"
            riskdata = ""
            terminalcountrycode = "0566"
            terminaltype = "34"
            terminalcapability = "E0F8C8"
            extendedterminalcapability = "E000F0A001"
            referercurrencycode = "0566"
            merchantcatcode = ""
        }


        val actualTerminalConfig = EmvUtils.getTerminalConfig(terminalXml)


        assertEquals(expectedTerminal.ddol, actualTerminalConfig.ddol)
        assertEquals(expectedTerminal.tdol, actualTerminalConfig.tdol)
        assertEquals(expectedTerminal.floorlimit, actualTerminalConfig.floorlimit)
        assertEquals(expectedTerminal.merchantcatcode, actualTerminalConfig.merchantcatcode)
        assertEquals(expectedTerminal.referercurrencycode, actualTerminalConfig.referercurrencycode)
        assertEquals(expectedTerminal.terminalcapability, actualTerminalConfig.terminalcapability)
        assertEquals(expectedTerminal.terminalcountrycode, actualTerminalConfig.terminalcountrycode)

    }
}