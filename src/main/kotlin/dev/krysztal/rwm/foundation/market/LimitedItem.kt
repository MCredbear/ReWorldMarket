package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class LimitedItem(val material: Material, val saleable: Boolean = true, val minPrice: Int?, val maxPrice: Int?, val recommendPrice: Int?)
