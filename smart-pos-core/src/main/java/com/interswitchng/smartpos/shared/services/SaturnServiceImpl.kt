package com.interswitchng.smartpos.shared.services

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryRequestHeaderModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.modules.main.transfer.utils.CipherUtil
import com.interswitchng.smartpos.modules.main.transfer.utils.HashUtils
import com.interswitchng.smartpos.shared.interfaces.library.SaturnService
import com.interswitchng.smartpos.shared.interfaces.retrofit.ISaturnService
import com.interswitchng.smartpos.shared.utilities.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class SaturnServiceImpl(private val saturnService: ISaturnService): SaturnService {

    val logger by lazy { Logger.with(this.javaClass.name) }

    override suspend fun nameEnquiry(
            bankCode: String,
            accountNumber: String): Optional<BeneficiaryModel> {
        val clientId = "IKIA9386DDAE1F2B112CE236CAA472A80A90F99B3987"
        val clientSecret = "E5jlYmDMw3nsPiNMI1Ys8fpmmHa6YRPEu675q6b6iFs="
        val valUrl = "https://saturn.interswitch.com/api/v1/nameenquiry/banks/${bankCode}/accounts/${accountNumber}"
        val nonce = HashUtils.generateGuid(8)
        val plainCipher = CipherUtil.generateSignatureCipherPlain("GET", valUrl,nonce,clientId,clientSecret)
        logger.log(plainCipher)
        val parameters = NameEnquiryRequestHeaderModel()
        parameters.authorisation = "InterswitchAuth SUtJQTkzODZEREFFMUYyQjExMkNFMjM2Q0FBNDcyQTgwQTkwRjk5QjM5ODc="
        parameters.clientId = clientId
        parameters.clientSecret = clientSecret
        parameters.nonce = nonce
        parameters.signature = CipherUtil.generateCipherHash(HashUtils.sha1(plainCipher))
        parameters.signatureMethod = "SHA1"
        parameters.host = "saturn.interswitch.com"
        parameters.timeStamp = HashUtils.getTimeStamp().toString()



        val nameEnquiry = saturnService.nameEnquiry(
                url =  "v1/nameenquiry/banks/$bankCode/accounts/$accountNumber",
                authorisation =  parameters.authorisation,
                clientId =  parameters.clientId,
                clientSecret = parameters.clientSecret,
                signature = parameters.signature.toString().trim(),
                signatureMethod = parameters.signatureMethod,
                timeStamp = parameters.timeStamp,
                nonce = parameters.nonce,
                host = parameters.host
        ).await()
        val nameEnquiryResponse = nameEnquiry.first
        return when (nameEnquiryResponse) {
            null -> None
            else -> Some(nameEnquiryResponse)
        }
    }

    private suspend fun <T> Simple<T>.await(): Pair<T?, String?> {
        return suspendCoroutine { continuation ->
            process { response, t ->
                val message =  t?.message ?: t?.localizedMessage

                // log errors
                if (message != null) logger.log(message)
                // pair result and error
                val result = Pair(response, message)
                // return response
                continuation.resume(result)
            }
        }
    }

}