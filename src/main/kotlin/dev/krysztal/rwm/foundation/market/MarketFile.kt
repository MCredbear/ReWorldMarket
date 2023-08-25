package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class MarketFile(
    var entry: Map<Material, MarketItem>?
)
