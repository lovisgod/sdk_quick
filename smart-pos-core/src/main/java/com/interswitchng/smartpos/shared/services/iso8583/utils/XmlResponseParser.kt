package com.interswitchng.smartpos.shared.services.iso8583.utils


import com.interswitchng.smartpos.shared.services.kimono.models.BillPaymentResponse
import com.interswitchng.smartpos.shared.services.kimono.models.PurchaseResponse
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class XmlPullParserHandler {
    private var purchaseResponse :PurchaseResponse = PurchaseResponse()

    private var text: String? = null

    fun contains(tagname:String):Boolean{
        return tagname.equals("reversalResponseWithoutOriginalDate", ignoreCase = true) || tagname.equals("reversalResponse", ignoreCase = true) || tagname.equals("completionResponse", ignoreCase = true) || tagname.equals("reservationResponse", ignoreCase = true) || tagname.equals("purchaseResponse", ignoreCase = true) || tagname.equals("channelResponse", ignoreCase = true) || tagname.equals("ifisBillPaymentCashoutResponse", ignoreCase = true)
    }
    fun parse(inputStream: InputStream): PurchaseResponse {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {

                    XmlPullParser.START_TAG -> if (contains(tagname)) {
                        // create a new instance of employee
                        purchaseResponse = PurchaseResponse()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (contains(tagname)) {
                        // add employee object to list
                       return  purchaseResponse
                    } else if (tagname.equals("authCode", ignoreCase = true)) {
                        purchaseResponse.authCode = text.toString()
                    } else if (tagname.equals("referenceNumber", ignoreCase = true)) {
                        purchaseResponse.referenceNumber = text.toString()
                    } else if (tagname.equals("stan", ignoreCase = true)) {
                        purchaseResponse.stan = text.toString()
                    }
                    else if (tagname.equals("transactionChannelName", ignoreCase = true)) {
                        purchaseResponse.transactionChannelName = text.toString()
                    }
                    else if (tagname.equals("field39", ignoreCase = true)) {
                        purchaseResponse.responseCode = text.toString()
                    }
                    else if (tagname.equals("description", ignoreCase = true)) {
                        purchaseResponse.description = text.toString()
                    }
                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return purchaseResponse
    }
}

class XmlPullParserHandlerBP {
    private var billPaymentResponse: BillPaymentResponse = BillPaymentResponse()

    private var text: String? = null

    fun contains(tagname: String): Boolean {
        return tagname.equals("reversalResponseWithoutOriginalDate", ignoreCase = true) || tagname.equals("reversalResponse", ignoreCase = true) || tagname.equals("completionResponse", ignoreCase = true) || tagname.equals("reservationResponse", ignoreCase = true) || tagname.equals("purchaseResponse", ignoreCase = true) || tagname.equals("channelResponse", ignoreCase = true) || tagname.equals("BillPaymentResponse", ignoreCase = true)
    }

    fun parse(inputStream: InputStream): BillPaymentResponse {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {

                    XmlPullParser.START_TAG -> if (contains(tagname)) {
                        // create a new instance of employee
                        billPaymentResponse = BillPaymentResponse()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (contains(tagname)) {
                        // add employee object to list
                        return billPaymentResponse
                    } else if (tagname.equals("authId", ignoreCase = true)) {
                        billPaymentResponse.authId = text.toString()
                    } else if (tagname.equals("stan", ignoreCase = true)) {
                        billPaymentResponse.stan = text.toString()
                    } else if (tagname.equals("field39", ignoreCase = true)) {
                        billPaymentResponse.responseCode = text.toString()
                    } else if (tagname.equals("description", ignoreCase = true)) {
                        billPaymentResponse.description = text.toString()
                    } else if (tagname.equals("transactionId", ignoreCase = true)) {
                        billPaymentResponse.transactionId = text.toString()
                    } else if (tagname.equals("uuid", ignoreCase = true)) {
                        billPaymentResponse.uuid = text.toString()
                    } else if (tagname.equals("transactionRef", ignoreCase = true)) {
                        billPaymentResponse.transactionRef = text.toString()
                    } else if (tagname.equals("retrievalRefNumber", ignoreCase = true)) {
                        billPaymentResponse.retrievalRefNumber = text.toString()
                    }

                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        }
        return billPaymentResponse
    }
}


class XmlPullParserHandlerBPInquiry {
    private var billPaymentResponse: BillPaymentResponse = BillPaymentResponse()

    private var text: String? = null

    fun contains(tagname: String): Boolean {
        return tagname.equals("reversalResponseWithoutOriginalDate", ignoreCase = true) || tagname.equals("reversalResponse", ignoreCase = true) || tagname.equals("completionResponse", ignoreCase = true) || tagname.equals("reservationResponse", ignoreCase = true) || tagname.equals("purchaseResponse", ignoreCase = true) || tagname.equals("channelResponse", ignoreCase = true) || tagname.equals("BillPaymentResponse", ignoreCase = true)
    }

    fun parse(inputStream: InputStream): BillPaymentResponse {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {

                    XmlPullParser.START_TAG -> if (contains(tagname)) {
                        // create a new instance of employee
                        billPaymentResponse = BillPaymentResponse()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (contains(tagname)) {
                        // add employee object to list
                        return billPaymentResponse
                    } else if (tagname.equals("authId", ignoreCase = true)) {
                        billPaymentResponse.authId = text.toString()
                    } else if (tagname.equals("stan", ignoreCase = true)) {
                        billPaymentResponse.stan = text.toString()
                    } else if (tagname.equals("field39", ignoreCase = true)) {
                        billPaymentResponse.responseCode = text.toString()
                    } else if (tagname.equals("description", ignoreCase = true)) {
                        billPaymentResponse.description = text.toString()
                    } else if (tagname.equals("transactionId", ignoreCase = true)) {
                        billPaymentResponse.transactionId = text.toString()
                    } else if (tagname.equals("uuid", ignoreCase = true)) {
                        billPaymentResponse.uuid = text.toString()
                    } else if (tagname.equals("transactionRef", ignoreCase = true)) {
                        billPaymentResponse.transactionRef = text.toString()
                    } else if (tagname.equals("retrievalRefNumber", ignoreCase = true)) {
                        billPaymentResponse.retrievalRefNumber = text.toString()
                    }

                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return billPaymentResponse
    }
}
