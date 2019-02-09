package com.igweze.ebi.paxemvcontact.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import com.igweze.ebi.paxemvcontact.POSDeviceService
import com.igweze.ebi.paxemvcontact.R
import com.igweze.ebi.paxemvcontact.posshim.CardService
import com.igweze.ebi.paxemvcontact.posshim.PosInterface
import com.igweze.ebi.paxemvcontact.utilities.DisplayUtils
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintStringConfiguration
import java.util.*

class PrintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
    }

    private lateinit var cardService: CardService
    private lateinit var pos: PosInterface

    override fun onStart() {
        super.onStart()

        // Looper.prepare()
        PosInterface.setDalInstance(applicationContext)
        cardService =  CardService.getInstance(applicationContext)
        pos = PosInterface.getInstance(cardService)

    }
}
