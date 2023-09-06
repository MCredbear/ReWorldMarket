package dev.krysztal.rwm.foundation.market

import dev.krysztal.rwm.foundation.database.Database
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object Market {
    private val itemEntry: MutableList<Item> = mutableListOf()
    private val limitationEntry: MutableList<Limitation> = mutableListOf()

    init {
        itemEntry.addAll(Database.getItemEntry())

    }

    fun sale(player: Player, itemStack: ItemStack, price: Long): Boolean {
        val limitation = limitationEntry.find { limitation ->
            limitation.material == itemStack.type
        }
        if (limitation != null) {
            if (limitation.saleable == true) {
                player.sendMessage("该物品不可出售")
                return false
            }
            if (limitation.minPrice?.let { it > (price / itemStack.amount) } == true) {
                player.sendMessage("你的出价太低了")
                return false
            }
            if (limitation.maxPrice?.let { it < (price / itemStack.amount) } == true) {
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

    // 添加限制
    fun addLimitation(player: Player, limitation: Limitation): Boolean {
        return if (Database.findLimitation(limitation.material) != null) {
            player.sendMessage("该物品已存在限制")
            false
        } else {
            Database.addLimitation(limitation)
            player.sendMessage("添加成功")
            true
        }
    }

    // 查找限制
    fun findLimitation(player: Player, material: Material) {
        val limitation = Database.findLimitation(material)
        if (limitation != null) {
            player.sendMessage(
                """
                [${material.toString()}]
                是否可以出售：
                最低单价：${limitation.minPrice}
                最高单价：${limitation.maxPrice}
                推荐单价：${limitation.recommendPrice}
                """.trimIndent().format()
            )
        } else {
            player.sendMessage("没有找到该物品的限制")
        }
    }

    // 修改限制
    fun setLimitation(player: Player, limitation: Limitation): Boolean {
        return if (Database.findLimitation(limitation.material) == null) {
            Database.addLimitation(limitation)
            player.sendMessage("该限制不存在，现在已添加")
            true
        } else {
            Database.setLimitation(limitation)
            player.sendMessage("修改成功")
            true
        }
    }

    // 移除限制
    fun removeLimitation(player: Player, material: Material): Boolean {
        val limitation = Database.findLimitation(material)
        return if (limitation != null) {
            Database.removeLimitation(material)
            player.sendMessage("移除成功")
            true
        } else {
            player.sendMessage("移除失败，没有该限制")
            false
        }
    }

    // 移除所有限制
    fun removeAllLimitation(player: Player): Boolean {
        Database.removeAllLimitation()
        player.sendMessage("移除成功")
        return true
    }
}