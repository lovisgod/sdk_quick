package com.interswitchng.smartpos.modules.main.models

data class RegisteredFingerprint (
    val userName: String,
    val userId: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegisteredFingerprint

        if (userName != other.userName) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}