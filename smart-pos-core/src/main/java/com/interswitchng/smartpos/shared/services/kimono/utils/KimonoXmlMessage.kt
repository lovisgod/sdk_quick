package com.interswitchng.smartpos.shared.services.kimono.utils

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class KimonoXmlMessage(val xmlDocument:Document) {


    fun getAttributeValuesByAttributeNameAndAttributeValue( attributeValue: String, attributeName: String, attributeValueName: String): List<String> {

        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        //<item type="T1" count="1">Value1</item>
        val xpath = "/ItemSet/Item[contains(@$attributeName, '$attributeValue')]"

        val doc: Document=xmlDocument;

        val itemsTypeT1 = xPath.evaluate(xpath, doc, XPathConstants.NODESET) as NodeList

        val itemList: MutableList<String> = ArrayList()
        for (i in 0..itemsTypeT1.length - 1) {

            val attributeValue = itemsTypeT1.item(i).attributes.getNamedItem(attributeValueName)
            val value = attributeValue.nodeValue

            itemList.add(attributeValue.nodeValue)
        }

        return ArrayList(itemList)
    }

    fun <T> getObjectValue(t: T): String? {



        return  ""
    }

    companion object {
        fun readXml(xmlMessage:String): KimonoXmlMessage {

            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlMessage))
            val doc = dBuilder.parse(xmlInput)

            return KimonoXmlMessage(doc);
        }
    }

}


// /purchaseResponse/description
// /purchaseResponse/field39
// /purchaseResponse/authId

//
//<purchaseResponse xmlns:ns2="http://interswitchng.com" xmlns:ns3="http://tempuri.org/ns.xsd">
//<description>Transaction Approved</description> <field39>00</field39>
//<authId>161117</authId>
//<hostEmvData>
//<AmountAuthorized>0</AmountAuthorized> <AmountOther>0</AmountOther> <atc>001B</atc> <iad>8A27FCA6008000000000000000000000</iad> <rc>00</rc>
//</hostEmvData> <referenceNumber>000005051503</referenceNumber> <stan>9</stan>
//<transactionChannelName>MY_NEW_PB</transactionChannelName> <wasReceive>true</wasReceive>
//<wasSend>true</wasSend>
//</purchaseResponse>