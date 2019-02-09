package com.igweze.ebi.paxemvcontact.emv

import com.pax.jemv.clcommon.EMV_APPLIST
import java.util.ArrayList

object EmvTestAIDList {

    private val PART_MATCH = 0
    private val FULL_MATCH = 1


    private val EMV = EmvTestAID(
            0,
            "",
            "A0000000999090",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    )

    private val VISA_VSDC = EmvTestAID(
            0,
            "VISA CREDIT",
            "A0000000031010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    )

    private val VISA_ELECTRON = EmvTestAID(
            0,
            "VISA ELECTRON",
            "A0000000032010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    )

    private val VISA_ELECTRON2 = EmvTestAID(
            0,
            "VISA ELECTRON2",
            "A0000000033010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    )


    private val MASTER_MCHIP = EmvTestAID(
            0,
            "MCHIP",
            "A0000000041010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    )

    private val MASTER_MAESTRO = EmvTestAID(
            0,
            "MAESTRO",
            "A0000000043060",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400800000",
            "F8502CF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    )

    private val MASTER_MAESTRO_US = EmvTestAID(
            0,
            "MAESTRO",
            "A0000000042203",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    )

    private val MASTER_CIRRUS = EmvTestAID(
            0,
            "CIRRUS",
            "A0000000046000",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    )

    private val MCC_4 = EmvTestAID(
            0,
            "",
            "A0000000046010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    )

    private val MCC_5 = EmvTestAID(
            0,
            "",
            "A0000000101030",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    )

    private val VERVE = EmvTestAID(
            0, "Verve",
            "A0000003710001",
            PART_MATCH,0, 0, 0, 0, 1, 1,
            0, 0,
            "0000000000",
            "CC00FC8000",
            "CC00FC8000",
            "000000123456",
            "9F3704",
            "N/A",
            "0001",
            null


    )

    fun genApplists(): List<EMV_APPLIST> {
        val applists = ArrayList<EMV_APPLIST>()

        applists.add(EMV.toAPPListItem())
        applists.add(VISA_VSDC.toAPPListItem())
        applists.add(VISA_ELECTRON.toAPPListItem())
        applists.add(VISA_ELECTRON2.toAPPListItem())
        applists.add(MASTER_MCHIP.toAPPListItem())
        applists.add(MASTER_MAESTRO.toAPPListItem())
        applists.add(MASTER_MAESTRO_US.toAPPListItem())
        applists.add(MASTER_CIRRUS.toAPPListItem())
        applists.add(MCC_4.toAPPListItem())
        applists.add(MCC_5.toAPPListItem())
        applists.add(VERVE.toAPPListItem())
        return applists
    }


}

