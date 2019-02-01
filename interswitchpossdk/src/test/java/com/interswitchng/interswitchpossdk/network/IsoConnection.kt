package com.interswitchng.interswitchpossdk.network

import androidx.test.platform.app.InstrumentationRegistry
import com.github.kpavlov.jreactive8583.IsoMessageListener
import com.github.kpavlov.jreactive8583.client.Iso8583Client
import com.interswitchng.interswitchpossdk.Utilities.getBytes
import com.solab.iso8583.IsoMessage
import com.solab.iso8583.MessageFactory
import com.solab.iso8583.parse.ConfigParser
import io.netty.channel.ChannelHandlerContext
import net.jodah.concurrentunit.Waiter
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.StringReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class IsoConnection {

    private val host = "196.6.103.124"
    private val port = 5043
    private val CLEAR_MASTER_KEY = "DBEECACCB4210977ACE73A1D873CA59F"
    private lateinit var client: Iso8583Client<IsoMessage>
    private val socketAddress = InetSocketAddress(host, port)
    private lateinit var messageFactory: MessageFactory<IsoMessage>


    @Before
    fun setup() {

        val data = getBytes("jpos.xml")
        val string = String(data)
        val stringReader = StringReader(string)
        messageFactory = ConfigParser.createFromReader(stringReader)
        messageFactory.isUseBinaryBitmap = false //NIBSS usebinarybitmap = false
        messageFactory.characterEncoding = "UTF-8"


        client = Iso8583Client<IsoMessage>(socketAddress, messageFactory)


        // initialize client
        client.init()

        // connect to endpoint
        client.connect(host, port)

    }

    @Test
    fun downloadMasterKey() {
        val waiter = Waiter()

        val now = Calendar.getInstance().time
        val timeAndDateFormatter = SimpleDateFormat("MMDDhhmmss", Locale.getDefault()) // field 7
        val timeFormatter = SimpleDateFormat("hhmmss", Locale.getDefault()) // field 12
        val dateFormatter = SimpleDateFormat("MMDD", Locale.getDefault()) // field 13

        if (client.isConnected) {
            val type = Integer.parseInt ("0800", 16)
            val message = messageFactory.newMessage(type)

            val getType = { field: Int -> message.getField<Any>(field).type }
            val getLength = { field: Int -> message.getField<Any>(field).length }



            // set values
            message.setValue(3, "9A0000", getType(3), getLength(3))
            message.setValue(7, timeAndDateFormatter.format(now), getType(7), getLength(7))
            message.setValue(11, "000327", getType(11), getLength(11))
            message.setValue(12, timeFormatter.format(now), getType(12), getLength(12))
            message.setValue(13, dateFormatter.format(now), getType(13), getLength(13))
            message.setValue(41, "20390007", getType(41), getLength(41))

            client.addMessageListener(object : IsoMessageListener<IsoMessage> {
                override fun onMessage(ctx: ChannelHandlerContext?, isoMessage: IsoMessage?): Boolean {
                    println("onMessage: " + isoMessage?.debugString())
                    waiter.resume()
                    return false
                }

                override fun applies(isoMessage: IsoMessage?): Boolean {
                    println("applies: " + isoMessage?.debugString())
                    return true
                }
            })

            try {
                println(message.debugString())
                client.send(message) // send synchronously

            } catch (ex: Throwable) {
                println("err: " + ex.localizedMessage)
            }
//
//      client.send(message, 1, TimeUnit.MINUTES) // send with timeout
            println("Done")

            waiter.await(2, TimeUnit.MINUTES)
        }
    }

    @After
    fun tearDown() {
        client.disconnect()
        client.shutdown()
    }
}