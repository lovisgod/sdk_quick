package com.interswitchng.smartpos.emv.pax

import com.interswitchng.smartpos.emv.pax.models.TerminalConfig
import com.interswitchng.smartpos.emv.pax.utilities.EmvUtils
import com.interswitchng.smartpos.emv.pax.utilities.StringUtils
import com.kulik.android.jaxb.library.Annotations.XmlRootElement
import com.kulik.android.jaxb.library.Annotations.XmlElement
import com.kulik.android.jaxb.library.parser.ParserImpl
import com.kulik.android.jaxb.library.parser.UnMarshalerTypes
import org.junit.Assert.assertEquals
import org.junit.Test

class TestFile {

    @XmlRootElement(name = "Simple")
    class SimpleClass {
        var myat: Int = 0
        @XmlElement(name = "x")
        var xAttribute: Int get() =  myat;
        set(value) {myat = value }
        @XmlElement(name = "y")
        var yAttribute: Int get() =  myat;
            set(value) {myat = value }
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
    fun intToHex() {
        val hexString = Integer.toHexString(2)
        val formatedString = String.format("0%s", hexString).toUpperCase()
        println("hex: $hexString")
        println("formatted: $formatedString")
    }

    @Test
    fun shouldParseXMLcorrectly() {

        val expected = SimpleClass().apply { xAttribute = 1; yAttribute = 2}
        val actual = ParserImpl(UnMarshalerTypes.JSONAdapter).parse(SimpleClass::class.java, "<Simple><x>1</x><y>2</y></Simple>")
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
        val expectedTerminal = TerminalConfig(true, false, 0, "0000000000",
                "CC00FC8000", "CC00FC8000", "9F3704", "N/A", "0001", "", "0566",
                "34", "E040C8", "E000F0A001", "0566", "")

        val actualTerminalConfig = EmvUtils.getTerminalConfig(terminalXml)

        assertEquals(expectedTerminal, actualTerminalConfig)
    }
}