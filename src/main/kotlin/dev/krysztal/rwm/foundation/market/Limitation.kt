package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Serializable
import org.bukkit.Material

// 对商品价格和是否可以出售的限制
@Serializable
data class Limitation(
    val material: Material,
    val saleable: Boolean?,
    val minPrice: Long?,
    val maxPrice: Long?,
    val recommendPrice: Long?
)
