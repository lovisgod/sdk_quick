package com.igweze.ebi.paxemvcontact.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.igweze.ebi.paxemvcontact.R
import com.igweze.ebi.paxemvcontact.posshim.CardService
import com.igweze.ebi.paxemvcontact.posshim.PosInterface

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
