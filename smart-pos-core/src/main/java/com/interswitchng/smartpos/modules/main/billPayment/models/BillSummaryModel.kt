package com.interswitchng.smartpos.modules.main.billPayment.models

data class BillSummaryModel(
        val summaryText: String,
        val details: ArrayList<BillDisplayDataModel>
)