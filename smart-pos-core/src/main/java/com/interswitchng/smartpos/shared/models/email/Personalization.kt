package com.interswitchng.smartpos.shared.models.email

data class Personalization(
        val to: List<Email>,
        val templateData: CustomArguments
)