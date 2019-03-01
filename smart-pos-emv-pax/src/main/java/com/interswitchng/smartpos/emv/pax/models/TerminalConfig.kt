package com.interswitchng.smartpos.emv.pax.models

import com.kulik.android.jaxb.library.Annotations.XmlRootElement
import com.kulik.android.jaxb.library.Annotations.XmlElement;



@XmlRootElement(name = "terminal")
internal data class TerminalConfig(
        @XmlElement(name = "supportpse")
        val supportpse: Boolean,
        @XmlElement(name = "floorlimitcheck")
        val floorlimitcheck: Boolean,
        @XmlElement(name = "floorlimit")
        val floorlimit: Int,
        @XmlElement(name = "tacdenial")
        val tacdenial: String,
        @XmlElement(name = "taconline")
        val taconline: String,
        @XmlElement(name = "tacdefault")
        val tacdefault: String,
        @XmlElement(name = "ddol")
        val ddol: String,
        @XmlElement(name = "tdol")
        val tdol: String,
        @XmlElement(name = "version")
        val version: String,
        @XmlElement(name = "riskdata")
        val riskdata: String,
        @XmlElement(name = "terminalcountrycode")
        val terminalcountrycode: String,
        @XmlElement(name = "terminaltype")
        val terminaltype: String,
        @XmlElement(name = "terminalcapability")
        val terminalcapability: String,
        @XmlElement(name = "extendedterminalcapability")
        val extendedterminalcapability: String,
        @XmlElement(name = "referercurrencycode")
        val referercurrencycode: String,
        @XmlElement(name = "merchantcatcode")
        val merchantcatcode: String
)