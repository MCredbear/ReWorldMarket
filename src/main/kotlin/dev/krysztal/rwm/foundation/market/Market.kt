package dev.krysztal.rwm.foundation.market

import dev.krysztal.rwm.foundation.database.Database
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object Market {
    private val itemEntry: MutableList<Item> = mutableListOf()
    private val limitedItemEntry: MutableList<LimitedItem> = mutableListOf()

    init {

    }

    fun sale(player: Player, itemStack: ItemStack, price: Long) :Boolean{
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
                if (saler !== null) {
                    val salerBalance = Database.getPlayerBalance(saler) + item.price
                    Database.setPlayerBalance(saler, salerBalance)
                }
                Database.removeItem(item)
                itemEntry.remove(item)
            }
        }

    }
}