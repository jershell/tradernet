package ru.tradernet.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Auth (
    val success: Boolean,
    @SerialName("SID") val sid: String,
    val userId: String
)