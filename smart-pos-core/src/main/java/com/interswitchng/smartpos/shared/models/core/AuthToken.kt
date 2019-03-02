package com.interswitchng.smartpos.shared.models.core

import com.google.gson.annotations.SerializedName


internal data class AuthToken(@SerializedName("access_token") val token: String)