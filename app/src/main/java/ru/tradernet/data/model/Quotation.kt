package ru.tradernet.data.model

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quotation(
    var prevLastTradePrice: Float? = null,
    @SerialName("c") val code: String,
    @SerialName("ltt") var timeOfLastTrade: String,
    @SerialName("ltr") @Optional var exchangeOfTheLatestTrade: String? = null, // MXC,
    @SerialName("pcp") @Optional var percentageChangeRelative: Float? = null,
    @SerialName("ltp") @Optional var lastTradePrice: Float? = null,
    @SerialName("chg") @Optional var changeInThePriceOfTheLastTradeInPoints: Float? = null
)