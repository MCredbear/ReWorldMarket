package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// UUID: The UUID of this item when it is added into market
@Serializable
data class Item(val uuid: String, @Contextual val itemStack: ItemStack, val price: Long, val player: Player?)
