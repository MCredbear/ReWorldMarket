package dev.krysztal.rwm.foundation.market

import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers.NBT
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Market {
    val itemEntry: MutableList<Item> = mutableListOf()
    val limitedItemEntry: MutableList<LimitedItem> = mutableListOf()

    init {

    }

    fun sale(player: Player,itemStack: ItemStack,price :Int){

    }

    fun buy(player: Player,itemStack: ItemStack){}
}