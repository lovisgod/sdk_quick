package com.interswitchng.smartpos.shared.models.transaction

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.TransactionInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionStatus
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.printer.slips.CardSlip
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.printer.slips.UssdQrSlip
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse

internal data class TransactionResult(
         val paymentType: PaymentType,
         val stan: String,
         val dateTime: String,
         val amount: String,
         val type: TransactionType,
         val cardPan: String,
         val cardType: String,
         val cardExpiry: String,
         val authorizationCode: String,
         val pinStatus: String,
         val responseMessage: String,
         val responseCode: String,
         val AID: String,
         val code: String,
         val telephone: String): Parcelable {


    constructor(parcel: Parcel) : this(
            getPaymentType(parcel.readInt()),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            getTransactionType(parcel.readInt()),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)


    fun getSlip(context: Context, terminal: TerminalInfo): TransactionSlip {
        return when (paymentType) {
            PaymentType.USSD, PaymentType.QR -> {
                val code = when (paymentType) {
                    PaymentType.USSD -> PrintObject.Data(code, PrintStringConfiguration(isBold = true, isTitle = true, displayCenter = true))
                    else -> CodeResponse.getBitmap(context, code)?.let { PrintObject.BitMap(it) }
                }
                UssdQrSlip(terminal, getTransactionStatus(), getTransactionInfo(), code)
            }
            PaymentType.Card, PaymentType.PayCode -> CardSlip(terminal, getTransactionStatus(), getTransactionInfo())
        }
    }


    /// function to extract
    /// print slip transaction info
     fun getTransactionInfo() =
            TransactionInfo(
                    paymentType,
                    stan,
                    dateTime,
                    amount,
                    type,
                    cardPan,
                    cardType,
                    cardExpiry,
                    authorizationCode,
                    pinStatus)


    /// function to extract
    /// print slip transaction status
     fun getTransactionStatus() =
            TransactionStatus(
                    responseMessage,
                    responseCode,
                    AID,
                    telephone)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(paymentType.ordinal)
        parcel.writeString(stan)
        parcel.writeString(dateTime)
        parcel.writeString(amount)
        parcel.writeInt(type.ordinal)
        parcel.writeString(cardPan)
        parcel.writeString(cardType)
        parcel.writeString(cardExpiry)
        parcel.writeString(authorizationCode)
        parcel.writeString(pinStatus)
        parcel.writeString(responseMessage)
        parcel.writeString(responseCode)
        parcel.writeString(AID)
        parcel.writeString(code)
        parcel.writeString(telephone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionResult> {
        override fun createFromParcel(parcel: Parcel): TransactionResult {
            return TransactionResult(parcel)
        }

        override fun newArray(size: Int): Array<TransactionResult?> {
            return arrayOfNulls(size)
        }

         fun getPaymentType(ordinal: Int): PaymentType {
            return when (ordinal) {
                PaymentType.Card.ordinal -> PaymentType.Card
                PaymentType.QR.ordinal -> PaymentType.QR
                PaymentType.USSD.ordinal -> PaymentType.USSD
                else -> PaymentType.PayCode
            }
        }

         fun getTransactionType(ordinal: Int): TransactionType {
            return TransactionType.Purchase
        }
    }

}