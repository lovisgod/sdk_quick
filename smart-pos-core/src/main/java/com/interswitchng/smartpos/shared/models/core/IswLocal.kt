package com.interswitchng.smartpos.shared.models.core

enum class IswLocal(internal val currency: String, internal val code: String) {
    GHANA("\u20B5", "936"),
    NIGERIA("NGN", "566"),
    USA("\u0024", "840")
}