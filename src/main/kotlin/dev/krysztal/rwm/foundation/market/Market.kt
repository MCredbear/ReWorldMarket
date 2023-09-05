package dev.krysztal.rwm.foundation.market

import dev.krysztal.rwm.ReWorldMarketMain
import dev.krysztal.rwm.foundation.database.Database
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object Market {
    private val itemEntry: MutableList<Item> = mutableListOf()
    private val limitedItemEntry: MutableList<LimitedItem> = mutableListOf()

    init {
        itemEntry.addAll(Database.getItemEntry())
        val limitedItemSections = ReWorldMarketMain.INSTANCE.config.getConfigurationSection("limited_item")
        if (limitedItemSections != null) {
            for (limitedItemKey in limitedItemSections.getKeys(false)) {
                val limitedItemSection = limitedItemSections.getConfigurationSection(limitedItemKey)
                limitedItemEntry.add(
                    LimitedItem(
                        Material.valueOf(limitedItemKey),
                        limitedItemSection!!.get("saleable") as? Boolean,
                        limitedItemSection.get("minPrice") as? Long,
                        limitedItemSection.get("maxPrice") as? Long,
                        limitedItemSection.get("recommendPrice") as? Long
                    )
                )
            }
        }
    }

    fun sale(player: Player, itemStack: ItemStack, price: Long): Boolean {
        val limitedItem = limitedItemEntry.find { limitedItem ->
            limitedItem.material == itemStack.type
        }
        if (limitedItem != null) {
            if (limitedItem.saleable == true) {
                player.sendMessage("该物品不可出售")
                return false
            }
            if (limitedItem.minPrice?.let { it > (price / itemStack.amount) } == true) {
                player.sendMessage("你的出价太低了")
                return false
            }
            if (limitedItem.maxPrice?.let { it < (price / itemStack.amount) } == true) {
                player.sendMessage("你的出价太高了")
                return false
            }
        }
        val item = Item(UUID.randomUUID().toString(), itemStack, price, player)
        Database.addItem(item)
        itemEntry.add(item)
        return true
    }

    fun buy(player: Player, item: Item) {
        val buyer = player
        val saler = item.player
        var buyerBalance = Database.getPlayerBalance(buyer)
        if (buyerBalance < item.price) {
            buyer.sendMessage("你的余额不足")
        } else {
            if (buyer.inventory.firstEmpty() == -1) {
                buyer.sendMessage("你的物品栏已满")
            } else {
                buyer.inventory.addItem(item.itemStack)
                buyerBalance -= item.price
                Database.setPlayerBalance(buyer, buyerBalance)
                if (saler != null) {
                    val salerBalance = Database.getPlayerBalance(saler) + item.price
                    Database.setPlayerBalance(saler, salerBalance)
                }
                Database.removeItem(item)
                itemEntry.remove(item)
            }
        }

    }
}