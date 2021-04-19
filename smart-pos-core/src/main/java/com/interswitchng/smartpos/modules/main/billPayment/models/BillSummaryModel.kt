package com.interswitchng.smartpos.modules.main.billPayment.models

data class BillSummaryModel(
        val image: Int? = null,
        val summaryText: String,
        val details: ArrayList<BillDisplayDataModel>
)