package ru.tradernet.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TopSecurities (
    val tickers: List<String>,
    val code: Int
)