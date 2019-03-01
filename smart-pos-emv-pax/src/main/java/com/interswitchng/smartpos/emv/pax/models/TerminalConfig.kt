package com.interswitchng.smartpos.emv.pax.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement


@JacksonXmlRootElement(localName = "terminal")
internal data class TerminalConfig(
        @JacksonXmlProperty(localName = "supportpse")
        val supportpse: Boolean,
        @JacksonXmlProperty(localName = "floorlimitcheck")
        val floorlimitcheck: Boolean,
        @JacksonXmlProperty(localName = "floorlimit")
        val floorlimit: Int,
        @JacksonXmlProperty(localName = "tacdenial")
        val tacdenial: String,
        @JacksonXmlProperty(localName = "taconline")
        val taconline: String,
        @JacksonXmlProperty(localName = "tacdefault")
        val tacdefault: String,
        @JacksonXmlProperty(localName = "ddol")
        val ddol: String,
        @JacksonXmlProperty(localName = "tdol")
        val tdol: String,
        @JacksonXmlProperty(localName = "version")
        val version: String,
        @JacksonXmlProperty(localName = "riskdata")
        val riskdata: String,
        @JacksonXmlProperty(localName = "terminalcountrycode")
        val terminalcountrycode: String,
        @JacksonXmlProperty(localName = "terminaltype")
        val terminaltype: String,
        @JacksonXmlProperty(localName = "terminalcapability")
        val terminalcapability: String,
        @JacksonXmlProperty(localName = "extendedterminalcapability")
        val extendedterminalcapability: String,
        @JacksonXmlProperty(localName = "referercurrencycode")
        val referercurrencycode: String,
        @JacksonXmlProperty(localName = "merchantcatcode")
        val merchantcatcode: String
)