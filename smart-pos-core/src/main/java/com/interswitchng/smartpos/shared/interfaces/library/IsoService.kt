package com.interswitchng.smartpos.shared.interfaces.library

import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse

/**
 * This interface provides operations that target the ISO-8385 message specification,
 * it provides functionality operations that require the ISO layer's method for communication.
 */
internal interface IsoService {

    /**
     * Uses the provided terminalId to perform key exchange with the EPMS server
     *
     * @param terminalId  a string representing the configured terminal id
     * @return     boolean expression indicating the success or failure status of the key exchange
     */
    fun downloadKey(terminalId: String, ip: String, port: Int): Boolean {
        return false
    }
    /**
     * Uses the provided terminalId to download the terminal information, like name and location.
     *
     * @param terminalId  a string representing the configured terminal id
     * @return  boolean expression indicating the success or failure of the terminal info download
     * @see TerminalInfo
     */
    fun downloadTerminalParameters(terminalId: String, ip: String, port: Int): Boolean {
        return false
    }

    /**
     * Initiates a card transaction using the provided terminal and transaction info, and returns the
     * transaction response provided by EPMS
     *
     * @param terminalInfo  the necessary information that identifies the current POS terminal
     * @param transaction  the purchase information required to perform the transaction
     * @return   response status indicating transaction success or failure
     */
    fun initiateCardPurchase(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?



    /**
     * Polls the server at intervals to show that the terminal is active, and returns the
     * response body
     *
     * @param terminalInfo  the necessary information that identifies the current POS terminal
     * @param pos  the Pos Information
     * @return   response status indicating transaction success or failure
     */
    suspend fun callHome(terminalInfo: TerminalInfo): Boolean

    /**
     * Initiates a paycode transaction using the provided code, terminal and payment info, and returns
     * a status response provided by the EPMS
     *
     * @param terminalInfo the necessary information that identifies the current POS terminal
     * @param code  the paycode that is generated by the customer's bank
     * @param paymentInfo the information required to make the current purchase
     * @return  response status indicating transaction success or failure
     */
    fun initiatePaycodePurchase(terminalInfo: TerminalInfo, code: String, paymentInfo: PaymentInfo): TransactionResponse?

    /**
     * Initiates a pre-authorization transaction using the provided terminal and transaction info, and returns the
     * transaction response provided by EPMS
     *
     * @param terminalInfo  the necessary information that identifies the current POS terminal
     * @param transaction  the purchase information required to perform the transaction
     * @return   response status indicating transaction success or failure
     */
    fun initiatePreAuthorization(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?

    /**
     * Initiates a completion transaction using the provided terminal and transaction info, and returns the
     * transaction response provided by EPMS
     *
     * @param terminalInfo  the necessary information that identifies the current POS terminal
     * @param transaction  the purchase information required to perform the transaction
     * @return   response status indicating transaction success or failure
     */
    fun initiateCompletion(terminalInfo: TerminalInfo, transaction: TransactionInfo): TransactionResponse?

}
