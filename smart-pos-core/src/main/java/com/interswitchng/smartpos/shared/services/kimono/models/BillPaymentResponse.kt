package com.interswitchng.smartpos.shared.services.kimono.models

import org.simpleframework.xml.*


@Root(name = "billPaymentResponse", strict = false)
@NamespaceList(
        Namespace(prefix = "ns2", reference = "http://interswitchng.com"),
        Namespace(prefix = "ns3", reference = "http://tempuri.org/ns.xsd")
)
data class BillPaymentResponse(
        @field:Element(name = "authId", required = false)
        var authId: String = "",

        @field:Element(name = "description", required = false)
        var description: String = "",

        @field:Element(name = "field39", required = false)
        var responseCode: String = "",

        @field:Path("var[1]")
        var responseCode_: String = "",

        @field:Element(name = "stan", required = false)
        var stan: String = "",

        @field:Element(name = "wasReceive", required = false)
        var wasReceive: Boolean = false,

        @field:Element(name = "wasSend", required = false)
        var wasSend: Boolean = false,

        @field:Element(name = "narration", required = false)
        var narration: String = "",

        @field:Element(name = "customerDescription", required = false)
        var customerDescription: String = "",

        @field:Element(name = "itemDescription", required = false)
        var itemDescription: String = "",

        @field:Element(name = "biller", required = false)
        var biller: String = "",

        @field:Element(name = "billerCode", required = false)
        var billerCode: String = "",

        @field:Element(name = "transactionRef", required = false)
        var transactionRef: String = "",

        @field:Element(name = "approvedAmount", required = false)
        var approvedAmount: String = "",

        @field:Element(name = "collectionsAccountNumber", required = false)
        var collectionsAccountNumber: String = "",

        @field:Element(name = "collectionsAccountType", required = false)
        var collectionsAccountType: String = "",

        @field:Element(name = "uuid", required = false)
        var uuid: String = "",

        @field:Element(name = "isAmountFixed", required = false)
        var isAmountFixed: String = "",

        @field:Element(name = "surcharge", required = false)
        var surcharge: String = "",

        @field:Element(name = "transactionId", required = false)
        var transactionId: String = "",

        @field:Element(name = "retrievalRefNumber", required = false)
        var retrievalRefNumber: String = "",

        @field:Element(name = "customerId", required = false)
        var customerId: String = "",

        @field:Element(name = "referenceNumber", required = false)
        var referenceNumber: String = ""

)



@Root
@NamespaceList(
        Namespace(prefix = "ns2", reference = "http://tempuri.org/ns.xsd"),
        Namespace(prefix = "ns3", reference = "http://interswitchng.com")
)
data class AirtimeResponse(
        @field:Element(name = "authId", required = false)
        var authId: String = "",

        @field:Element(name = "description", required = false)
        var description: String = "",

        @field:Element(name = "field39", required = false)
        var responseCode: String = "",

        @field:Path("var[1]")
        @field:Element(name = "responsecode", required = false)
        var responseCode_: String = "",

        @field:Element(name = "stan", required = false)
        var stan: String = "",

        @field:Element(name = "wasReceive", required = false)
        var wasReceive: Boolean = false,

        @field:Element(name = "wasSend", required = false)
        var wasSend: Boolean = false,

        @field:Element(name = "narration", required = false)
        var narration: String = "",

        @field:Element(name = "customerDescription", required = false)
        var customerDescription: String = "",

        @field:Element(name = "itemDescription", required = false)
        var itemDescription: String = "",

        @field:Element(name = "biller", required = false)
        var biller: String = "",

        @field:Element(name = "billerCode", required = false)
        var billerCode: String = "",

        @field:Element(name = "transactionRef", required = false)
        var transactionRef: String = "",

        @field:Element(name = "approvedAmount", required = false)
        var approvedAmount: String = "",

        @field:Element(name = "collectionsAccountNumber", required = false)
        var collectionsAccountNumber: String = "",

        @field:Element(name = "collectionsAccountType", required = false)
        var collectionsAccountType: String = "",

        @field:Element(name = "uuid", required = false)
        var uuid: String = "",

        @field:Element(name = "isAmountFixed", required = false)
        var isAmountFixed: String = "",

        @field:Element(name = "surcharge", required = false)
        var surcharge: String = "",

        @field:Element(name = "transactionId", required = false)
        var transactionId: String = "",

        @field:Element(name = "retrievalRefNumber", required = false)
        var retrievalRefNumber: String = "",

        @field:Element(name = "customerId", required = false)
        var customerId: String = "",

        @field:Element(name = "referenceNumber", required = false)
        var referenceNumber: String = ""

)
