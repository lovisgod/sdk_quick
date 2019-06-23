package com.interswitchng.smartpos.shared.models.email


import com.interswitchng.smartpos.shared.Constants.EMAIL_TEMPLATE_ID
//import com.sendgrid.helpers.mail.objects.Email
//import com.sendgrid.helpers.mail.objects.Personalization


data class EmailMessage (
//        val from: Email,
//        val personalizations: List<Personalization>,
        val customArguments: CustomArguments,
        val template_id: String = EMAIL_TEMPLATE_ID
)