package dev.krysztal.rwm.foundation.database

import dev.krysztal.rwm.foundation.infix.add
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
class MarketItemEntry(
    private val overallOnSales: Int,
) : AbstractItemEntry() {

    override fun getLoreComponent(): List<Component> {
        val componentList = mutableListOf(
            Component.translatable("rwm.market.overallOnSales") add " : " add overallOnSales,

            Component.text(" "),
        )

        componentList.addAll(super.getLoreComponent())
        return componentList
    }
}
