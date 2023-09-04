package dev.krysztal.rwm

import dev.krysztal.rwm.foundation.command.ReWorldMarketCommandExecutor
import dev.krysztal.rwm.foundation.database.MarketDatabase
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

@Suppress("EmptyDefaultConstructor")
class ReWorldMarketMain() : JavaPlugin() {

    private val logger = getLogger()

    override fun onLoad() {
        INSTANCE = this
    }

    override fun onEnable() {
        Metrics(this, bStatsID)
        this.saveDefaultConfig()
        MarketDatabase.database

        val command = getCommand("market")
        if (command == null) {
            logger.info("注册命令失败，请检查配置文件")
        } else {
            val commandExecutor = ReWorldMarketCommandExecutor()
            command.setExecutor(commandExecutor)
        }

    }

    override fun onDisable() {
        MarketDatabase.database
    }

    companion object {
        lateinit var INSTANCE: ReWorldMarketMain
        private const val bStatsID = 19574
    }
}
