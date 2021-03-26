package com.interswitchng.smartpos.modules.main.billPayment.models

data class AirtimeRechargeModel(
        private val network: NetworksModel,
        private val amount: String,
        private  val phoneNumber: String
)
