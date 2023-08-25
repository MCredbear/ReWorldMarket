package dev.krysztal.rwm.foundation.database

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material

@Serializable
abstract class AbstractItemEntry(
    open val material: Material = Material.DIRT,
    open val lore: MutableList<String> = mutableListOf(),
) {

    open fun getLoreComponent() =
        lore.map { MiniMessage.miniMessage().deserialize(it).asComponent() }.toList()

    open fun appendLore(message: String, index: Int? = null) =
        lore.add(index ?: lore.size, message)
}
