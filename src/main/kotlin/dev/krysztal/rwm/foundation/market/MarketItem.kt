package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Serializable

@Serializable
data class MarketItem(val min: Int?, val max: Int?, val recommend: Int?)
