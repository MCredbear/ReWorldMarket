@file:Suppress("detekt:all")

package dev.krysztal.rwm.foundation.database

import dev.krysztal.rwm.foundation.market.Limitation
import org.bukkit.entity.Player
import java.util.*

object MarketOperation {
    val itemEntries: MutableList<Limitation> = mutableListOf()

    fun refreshItemEntries() {
    }

    fun putItemEntry() {
    }

    fun removeItemEntry() {
    }

    fun buyGoods(
        player: Player,
        uuid: UUID,
        price: Int
    ) {
    }

    fun putGoods(
        player: Player,
        uuid: UUID,
        price: Int
    ) {
        val item = player.inventory.itemInMainHand

    }
}
