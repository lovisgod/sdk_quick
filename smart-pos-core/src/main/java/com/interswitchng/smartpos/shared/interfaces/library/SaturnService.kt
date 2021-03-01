package com.interswitchng.smartpos.shared.interfaces.library

import com.gojuno.koptional.Optional
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryRequestHeaderModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse

internal interface SaturnService {
    suspend fun nameEnquiry(
            bankCode: String,
            accountNumber: String
    ): Optional<NameEnquiryResponse>
}