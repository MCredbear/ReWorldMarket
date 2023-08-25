package dev.krysztal.rwm

import dev.krysztal.rwm.foundation.database.MarketDatabase
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

@Suppress("EmptyDefaultConstructor")
class ReWorldMarketMain() : JavaPlugin() {

    override fun onLoad() {
        INSTANCE = this
    }

    override fun onEnable() {
        Metrics(this, bStatsID)
        this.saveDefaultConfig()
        MarketDatabase.database
    }

    override fun onDisable() {
        MarketDatabase.database
    }

    companion object {
        lateinit var INSTANCE: ReWorldMarketMain
        private const val bStatsID = 19574
    }
}
