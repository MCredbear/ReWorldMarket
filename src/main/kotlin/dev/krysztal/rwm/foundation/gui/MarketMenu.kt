package dev.krysztal.rwm.foundation.gui

import dev.krysztal.rwm.foundation.market.Item
import dev.krysztal.rwm.foundation.market.Market
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

object MarketMenu : Listener {

    class MenuHolder(private val player: Player) : InventoryHolder {

        var offset: Int = 0

        private lateinit var inventory: Inventory

        var itemEntry: MutableList<Item> = mutableListOf()

        override fun getInventory(): Inventory {
            return inventory
        }

        fun show() {
            val title =
                Component.text("交易菜单-当前页面：${offset + 1}/${(Market.itemEntry.size / (4 * 9)) + 1}")
            inventory = Bukkit.createInventory(this, 5 * 9, title)

            itemEntry = Market.itemEntry.subList((4 * 9) * offset, (4 * 9) * (offset + 1))
            for (index in 0..4 * 9) {
                val itemStack = itemEntry[index].itemStack.clone()
                val priceText = Component.text("价格：${itemEntry[index].price}")
                val lore: MutableList<Component> = ((itemStack.lore() ?: mutableListOf()) + priceText).toMutableList()
                itemStack.lore(lore)
                inventory.setItem(index, itemStack)
            }

            val divider = ItemStack(Material.GLASS_PANE)
            divider.itemMeta = divider.itemMeta.apply { displayName(Component.text("")) }

            if (offset > 0) {
                val preButton = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
                preButton.itemMeta = preButton.itemMeta.apply { displayName(Component.text("上一页")) }
                inventory.addItem(preButton)
            } else {
                inventory.addItem(divider)
            }

            for (i in 0..3) {
                inventory.addItem(divider)
            }

            val sorter = ItemStack(Material.OAK_FENCE)
            sorter.itemMeta = sorter.itemMeta.apply { displayName(Component.text("排序")) }
            inventory.addItem(sorter)

            for (i in 0..3) {
                inventory.addItem(divider)
            }

            if (offset < (Market.itemEntry.size / (4 * 9))) {
                val nextButton = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
                nextButton.itemMeta = nextButton.itemMeta.apply { displayName(Component.text("上一页")) }
                inventory.addItem(nextButton)
            } else {
                inventory.addItem(divider)
            }

            player.openInventory(inventory)
        }

    }

    fun open(player: Player) {
        val menuHolder = MenuHolder(player)
        menuHolder.itemEntry = Market.itemEntry.subList(0, 4 * 9)
        menuHolder.show()
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory.holder is MenuHolder) {
            event.isCancelled = true

            val itemStack = event.currentItem ?: return
            val holder = event.inventory.holder as MenuHolder
            when ((itemStack.itemMeta.displayName() as TextComponent).content()) {
                "上一页" -> {
                    holder.offset -= 1
                    holder.show()
                }

                "下一页" -> {
                    holder.offset += 1
                    holder.show()
                }

                "排序" -> {
                    TODO("完成排序")
                }

                "" -> {}
                else -> {
                    val player = event.whoClicked as Player
                    val index = event.inventory.first(itemStack)
                    val item = holder.itemEntry[index]
                    Market.buy(player, item)
                }
            }
        }
    }
}
