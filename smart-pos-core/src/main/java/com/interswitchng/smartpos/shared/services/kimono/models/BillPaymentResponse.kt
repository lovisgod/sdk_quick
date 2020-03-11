package com.interswitchng.smartpos.shared.services.kimono.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root


@Root(name = "purchaseResponse", strict = false)
@NamespaceList(
        Namespace(prefix = "ns2", reference = "http://interswitchng.com"),
        Namespace(prefix = "ns3", reference = "http://tempuri.org/ns.xsd")
)
data class BillPaymentResponse(
        @field:Element(name = "stan", required = false)
        var stan: String = "",

        @field:Element(name = "wasReceive", required = false)
        var wasReceive: Boolean = false,

        @field:Element(name = "wasSend", required = false)
        var wasSend: Boolean = false,

        @field:Element(name = "responseCode", required = false)
        var responseCode: String = "",

        @field:Element(name = "responseMessage", required = false)
        var description: String = "",

        @field:Element(name = "transactionAmount", required = false)
        var transactionAmount: String = "",

        @field:Element(name = "remoteResponseCode", required = false)
        var remoteResponseCode: String = "",

        @field:Element(name = "approvedAmount", required = false)
        var approvedAmount: String = "",

        @field:Element(name = "surcharge", required = false)
        var surcharge: String = "",

        @field:Element(name = "transactionId", required = false)
        var transactionId: String = ""
)
