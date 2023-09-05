package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Serializable
import org.bukkit.Material

// 需要控制价格和是否可以出售的物品
@Serializable
data class LimitedItem(
    val material: Material,
    val saleable: Boolean?,
    val minPrice: Long?,
    val maxPrice: Long?,
    val recommendPrice: Long?
)
