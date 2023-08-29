package dev.krysztal.rwm.foundation.market

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// UUID: 这个物品第一次被加入市场时获得的UUID
@Serializable
data class Item(val uuid: String, @Contextual val itemStack: ItemStack, val price: Long, val player: Player?)
