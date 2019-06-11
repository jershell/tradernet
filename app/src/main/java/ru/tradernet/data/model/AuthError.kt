package ru.tradernet.data.model

import kotlinx.serialization.Serializable


@Serializable
data class AuthError (val Error: String)