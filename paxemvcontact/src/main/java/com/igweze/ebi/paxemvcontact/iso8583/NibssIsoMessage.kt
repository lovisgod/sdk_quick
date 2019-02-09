package com.igweze.ebi.paxemvcontact.iso8583

import com.solab.iso8583.IsoMessage
import java.io.PrintStream

class NibssIsoMessage(val message: IsoMessage)  {


    fun setValue(fieldId: Int, value: String): NibssIsoMessage {
        val field = message.getField<Any>(fieldId)
        message.setValue(fieldId, value, field.type, field.length)
        return this
    }

    fun dump(p: PrintStream) {

        val type = message.type.toByte()
        p.println("<isomsg mti=\"$type\">")

        for (i in 0 until 129) {
            val field = message.getField<Any>(i)
            if (field != null) {
                field.toString()
                val value = "\t\t <field id=\"$i\"  value=\"$field\" />"
                p.println(value)
            }
        }

        p.println( "</isomsg>")

    }
}