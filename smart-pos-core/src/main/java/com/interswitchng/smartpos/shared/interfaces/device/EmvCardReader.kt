package com.interswitchng.smartpos.shared.interfaces.device

import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardDetail
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse


/**
 * This interface is responsible for providing the EMV card chip transactions functionality
 * as provided by the device's CardReader SDK. It provides a way to setup, process, and
 * cancel transactions.
 */
interface EmvCardReader {

    /**
     * Sets the callback [EmvCallback] implementation, for the EmvCardRead to interact
     * with. The callback provides hook points to notify the consumer of state and status
     * of the triggered transaction.
     *
     * @param callback  a callback implementation that the reader communicates its status with
     * @see EmvCallback
     */
    fun setEmvCallback(callback: EmvCallback)

    /**
     * Removes the callback [EmvCallback] implementation that was set to communicate with the
     * EmvCardReader instance.
     *
     * @param callback the callback implementation that was set to be used to communicate with the reader
     * @see EmvCallback
     */
    fun removeEmvCallback(callback: EmvCallback)


    /**
     * Sets up a new transaction to be processed, providing the amount and terminal information
     * to be used to trigger the new transaction. The amount should be provided in its lowest
     * denomination of whatever currency the terminal is set to process e.g. kobo for Nigerian Naira
     *
     * @param amount  the cost in integer for the new transaction
     * @param terminalInfo  the terminal information to required to configure the device for the new transaction
     * @see TerminalInfo
     */
    fun setupTransaction(amount: Int, terminalInfo: TerminalInfo)


    /**
     * Starts the new transaction that has be setup. It uses the transaction amount and terminal information to
     * communicate with the card's chip, in order to perform the transaction. This returns an result
     * status [EmvResult] indicating what to do about the new transaction.
     *
     * @return   a emv result status indicating what action to take regarding the transaction
     * @see EmvResult
     */
    fun startTransaction(): EmvResult


    /**
     * Completes a transaction with the provided response from the EPMS server, which the reader will present to the
     * card to authenticate the server, and it returns the status of the card's authorization.
     *
     * @param response  the response from the server for the processed transaction
     * @return   a result indicating the status of the server authentication
     * @see EmvResult
     */
    fun completeTransaction(response: TransactionResponse): EmvResult

    /**
     * Cancels the transaction that has been setup
     */
    fun cancelTransaction()

    /**
     * Retrieves the Emv data read from the card and terminal, to packed into a message that will be delivered to the
     * EPMS server for processing the transaction. This method returns null if the card is not readable or no Application
     * can be selected for this card type.
     *
     * @return  emv data that contains information of the card, transaction, and terminal
     * @see EmvData
     */
    fun getTransactionInfo(): EmvData?

}