@file:Suppress("detekt:all")

package dev.krysztal.rwm.foundation.database

import dev.krysztal.rwm.foundation.market.MarketItem
import org.bukkit.entity.Player
import java.util.*

object MarketOperation {
    val itemEntries: MutableList<MarketItem> = mutableListOf()

    fun refreshItemEntries() {
    }

    fun putItemEntry() {
    }

    fun removeItemEntry() {
    }

    fun buyGoods(
        player: Player,
        uuid: UUID,
        count: Int = 1
    ) {
    }

    fun putGoods(
        player: Player,
        uuid: UUID,
        count: Int = 1,
    ) {
    }
}
