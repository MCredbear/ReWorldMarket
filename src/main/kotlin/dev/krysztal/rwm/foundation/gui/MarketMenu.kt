package dev.krysztal.rwm.foundation.gui

import dev.krysztal.rwm.foundation.market.Item
import dev.krysztal.rwm.foundation.market.Market
import net.kyori.adventure.text.Component
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

        enum class SortType {
            ASCENDING_ORDER_BY_TIME,
            DESCENDING_ORDER_BY_TIME,
            ASCENDING_ORDER_BY_UNIT_PRICE,
            DESCENDING_ORDER_BY_UNIT_PRICE,
            ASCENDING_ORDER_BY_TOTAL_PRICE,
            DESCENDING_ORDER_BY_TOTAL_PRICE
        }

        lateinit var sortType: SortType

        private lateinit var inventory: Inventory

        var itemEntry: List<Item> = listOf()

        internal val divider = ItemStack(Material.GLASS_PANE)
        internal val preButton = ItemStack(Material.WHITE_STAINED_GLASS_PANE)
        internal val sortByTimeButton = ItemStack(Material.OAK_FENCE)
        internal val sortByUnitPriceButton = ItemStack(Material.OAK_FENCE)
        internal val sortByTotalPriceButton = ItemStack(Material.OAK_FENCE)
        internal val nextButton = ItemStack(Material.WHITE_STAINED_GLASS_PANE)

        // 排序商品（获取当前页面的新商品列表）
        fun sort(sortType: SortType) {
            this.sortType = sortType
            when (sortType) {
                SortType.DESCENDING_ORDER_BY_TIME -> {
                    itemEntry = Market.itemEntry.reversed().slice((4 * 9) * offset..(4 * 9) * (offset + 1)).toList()
                    sortByTimeButton.lore(
                        mutableListOf(
                            Component.text("当前：按时间降序") as Component,
                            Component.text("点击切换成按时间升序") as Component
                        )
                    )
                    sortByUnitPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按时间降序") as Component,
                            Component.text("点击切换成按单价升序") as Component
                        )
                    )
                    sortByTotalPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按时间降序") as Component,
                            Component.text("点击切换成按总价升序") as Component
                        )
                    )
                }

                SortType.ASCENDING_ORDER_BY_TIME -> {
                    itemEntry = Market.itemEntry.slice((4 * 9) * offset..(4 * 9) * (offset + 1)).toList()
                    sortByTimeButton.lore(
                        mutableListOf(
                            Component.text("当前：按时间升序") as Component,
                            Component.text("点击切换成按时间降序") as Component
                        )
                    )
                    sortByUnitPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按时间升序") as Component,
                            Component.text("点击切换成按单价升序") as Component
                        )
                    )
                    sortByTotalPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按时间升序") as Component,
                            Component.text("点击切换成按总价升序") as Component
                        )
                    )
                }

                SortType.ASCENDING_ORDER_BY_UNIT_PRICE -> {
                    itemEntry = Market.itemEntry.sortedBy { it.price.toDouble() / it.itemStack.amount }
                        .slice((4 * 9) * offset..(4 * 9) * (offset + 1)).toList()
                    sortByTimeButton.lore(
                        mutableListOf(
                            Component.text("当前：按单价升序") as Component,
                            Component.text("点击切换成按时间降序") as Component
                        )
                    )
                    sortByUnitPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按单价升序") as Component,
                            Component.text("点击切换成按单价降序") as Component
                        )
                    )
                    sortByTotalPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按单价升序") as Component,
                            Component.text("点击切换成按总价升序") as Component
                        )
                    )
                }

                SortType.DESCENDING_ORDER_BY_UNIT_PRICE -> {
                    itemEntry = Market.itemEntry.sortedByDescending { it.price.toDouble() / it.itemStack.amount }
                        .slice((4 * 9) * offset..(4 * 9) * (offset + 1)).toList()
                    sortByTimeButton.lore(
                        mutableListOf(
                            Component.text("当前：按单价降序") as Component,
                            Component.text("点击切换成按时间降序") as Component
                        )
                    )
                    sortByUnitPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按单价降序") as Component,
                            Component.text("点击切换成按单价升序") as Component
                        )
                    )
                    sortByTotalPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按单价降序") as Component,
                            Component.text("点击切换成按总价升序") as Component
                        )
                    )
                }

                SortType.ASCENDING_ORDER_BY_TOTAL_PRICE -> {
                    itemEntry =
                        Market.itemEntry.sortedBy { it.price }.slice((4 * 9) * offset..(4 * 9) * (offset + 1)).toList()
                    sortByTimeButton.lore(
                        mutableListOf(
                            Component.text("当前：按总价升序") as Component,
                            Component.text("点击切换成按时间降序") as Component
                        )
                    )
                    sortByUnitPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按总价升序") as Component,
                            Component.text("点击切换成按单价升序") as Component
                        )
                    )
                    sortByTotalPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按总价升序") as Component,
                            Component.text("点击切换成按总价降序") as Component
                        )
                    )
                }

                SortType.DESCENDING_ORDER_BY_TOTAL_PRICE -> {
                    itemEntry =
                        Market.itemEntry.sortedByDescending { it.price }.slice((4 * 9) * offset..(4 * 9) * (offset + 1))
                            .toList()
                    sortByTimeButton.lore(
                        mutableListOf(
                            Component.text("当前：按总价降序") as Component,
                            Component.text("点击切换成按时间降序") as Component
                        )
                    )
                    sortByUnitPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按总价降序") as Component,
                            Component.text("点击切换成按单价升序") as Component
                        )
                    )
                    sortByTotalPriceButton.lore(
                        mutableListOf(
                            Component.text("当前：按总价降序") as Component,
                            Component.text("点击切换成按总价升序") as Component
                        )
                    )
                }
            }
        }

        init {
            divider.itemMeta = divider.itemMeta.apply { displayName(Component.text("")) }
            preButton.itemMeta = preButton.itemMeta.apply { displayName(Component.text("上一页")) }
            nextButton.itemMeta = nextButton.itemMeta.apply { displayName(Component.text("上一页")) }
            sortByTimeButton.itemMeta = sortByTimeButton.itemMeta.apply { displayName(Component.text("按时间排序")) }
            sortByUnitPriceButton.itemMeta =
                sortByUnitPriceButton.itemMeta.apply { displayName(Component.text("按单价排序")) }
            sortByTotalPriceButton.itemMeta =
                sortByTotalPriceButton.itemMeta.apply { displayName(Component.text("按总价排序")) }
            sort(SortType.DESCENDING_ORDER_BY_TIME)
        }

        override fun getInventory(): Inventory {
            return inventory
        }

        fun show() {
            val title =
                Component.text("交易菜单-当前页面：${offset + 1}/${(Market.itemEntry.size / (4 * 9)) + 1}")
            inventory = Bukkit.createInventory(this, 5 * 9, title)

            sort(SortType.DESCENDING_ORDER_BY_TIME)
            for (index in 0..itemEntry.size) {
                val itemStack = itemEntry[index].itemStack.clone()
                val priceText = Component.text("价格：${itemEntry[index].price}")
                val lore: List<Component> = ((itemStack.lore() ?: mutableListOf()) + priceText).toList()
                itemStack.lore(lore)
                inventory.setItem(index, itemStack)
            }


            if (offset > 0) {
                inventory.setItem(4 * 9, preButton)
            } else {
                inventory.setItem(4 * 9, divider)
            }

            inventory.setItem(4 * 9 + 1, divider)
            inventory.setItem(4 * 9 + 2, divider)

            inventory.setItem(4 * 9 + 4, sortByTimeButton)
            inventory.setItem(4 * 9 + 5, sortByUnitPriceButton)
            inventory.setItem(4 * 9 + 6, sortByTotalPriceButton)

            inventory.setItem(4 * 9 + 7, divider)
            inventory.setItem(4 * 9 + 8, divider)

            if (offset < (Market.itemEntry.size / (4 * 9))) {
                inventory.setItem(4 * 9 + 9, nextButton)
            } else {
                inventory.setItem(4 * 9 + 9, divider)
            }

            player.openInventory(inventory)
        }

    }

    fun open(player: Player) {
        val menuHolder = MenuHolder(player)
        menuHolder.show()
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory.holder is MenuHolder) {
            event.isCancelled = true

            val itemStack = event.currentItem ?: return
            val holder = event.inventory.holder as MenuHolder
            when (itemStack) {
                holder.preButton -> {
                    holder.offset -= 1
                    holder.show()
                }

                holder.nextButton -> {
                    holder.offset += 1
                    holder.show()
                }

                holder.sortByTimeButton -> {
                    when (holder.sortType) {
                        MenuHolder.SortType.DESCENDING_ORDER_BY_TIME -> holder.sort(MenuHolder.SortType.ASCENDING_ORDER_BY_TIME)
                        MenuHolder.SortType.ASCENDING_ORDER_BY_TIME -> holder.sort(MenuHolder.SortType.DESCENDING_ORDER_BY_TIME)
                        else -> holder.sort(MenuHolder.SortType.DESCENDING_ORDER_BY_TIME)
                    }
                }

                holder.sortByUnitPriceButton -> {
                    when (holder.sortType) {
                        MenuHolder.SortType.DESCENDING_ORDER_BY_UNIT_PRICE -> holder.sort(MenuHolder.SortType.ASCENDING_ORDER_BY_UNIT_PRICE)
                        MenuHolder.SortType.ASCENDING_ORDER_BY_UNIT_PRICE -> holder.sort(MenuHolder.SortType.DESCENDING_ORDER_BY_UNIT_PRICE)
                        else -> holder.sort(MenuHolder.SortType.ASCENDING_ORDER_BY_UNIT_PRICE)
                    }
                }

                holder.sortByTotalPriceButton -> {
                    when (holder.sortType) {
                        MenuHolder.SortType.DESCENDING_ORDER_BY_TOTAL_PRICE -> holder.sort(MenuHolder.SortType.ASCENDING_ORDER_BY_TOTAL_PRICE)
                        MenuHolder.SortType.ASCENDING_ORDER_BY_TOTAL_PRICE -> holder.sort(MenuHolder.SortType.DESCENDING_ORDER_BY_TOTAL_PRICE)
                        else -> holder.sort(MenuHolder.SortType.ASCENDING_ORDER_BY_TIME)
                    }
                }

                holder.divider -> {}
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
