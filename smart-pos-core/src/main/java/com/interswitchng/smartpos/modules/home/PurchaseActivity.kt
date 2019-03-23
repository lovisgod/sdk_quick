package com.interswitchng.smartpos.modules.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.errors.NotConfiguredException
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import kotlinx.android.synthetic.main.isw_activity_purchase.*
import java.text.NumberFormat

class PurchaseActivity : AppCompatActivity(), Keyboard.KeyBoardListener {

    private val logger by lazy { Logger.with("PurchaseActivity") }
    private lateinit var keyboard: Keyboard
    private val defaultAmount = "0.00"
    private var current = ""
    private var currentAmount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_purchase)

        amount.text = defaultAmount
        keyboard = Keyboard(this, this)
    }


    override fun onTextChange(text: String) {
        if (text != current) {

            val textAmount = if (text.isEmpty()) defaultAmount else text
            val cleanString = textAmount.replace("[$,.]".toRegex(), "")

            val parsed = java.lang.Double.parseDouble(cleanString)
            val numberFormat = NumberFormat.getInstance()
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2
            val formatted = numberFormat.format(parsed / 100)

            amount.text = formatted
            currentAmount = Integer.valueOf(cleanString)
            current = cleanString
            keyboard.setText(cleanString)
        }
    }

    override fun onSubmit(text: String) {
        val enteredAmount = amount.text.toString()
        if (enteredAmount.isEmpty() || enteredAmount == defaultAmount) {
            toast("Amount value is required")
        } else {
            makePayment(currentAmount, null)
        }
    }

    private fun makePayment(amount: Int, type: PaymentType?) {
        try {
            // trigger payment
            IswPos.getInstance().initiatePayment(this, amount, type)
        } catch (e: NotConfiguredException) {
            val message = "Pos has not been configured"
            toast(message)
            logger.log(e.message ?: e.localizedMessage ?: message)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            // handle success
            if (data != null) {
                val result = IswPos.getResult(data)
                logger.log(result.toString())
                if (result.responseCode == IsoUtils.OK) {
                    toast("Transaction Successful")
                    // reset the amount back to default
                    onTextChange(defaultAmount)
                } else {
                    toast("Transaction was not successful try again.")
                }
            }
        } else {
            // else handle error
            toast("Transaction was not completed.")
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}
