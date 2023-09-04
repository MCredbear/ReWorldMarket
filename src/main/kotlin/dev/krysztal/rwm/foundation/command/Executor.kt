package dev.krysztal.rwm.foundation.command

import dev.krysztal.rwm.foundation.market.Market
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReWorldMarketCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("你不是玩家")
            return false
        }
        return when (args?.get(0)) {
            "help" -> help(sender)
            "sale" -> sale(sender, args)
            "buy" -> buy(sender)
            else -> {
                sender.sendMessage("参数错误")
                false
            }
        }
    }

    private fun help(sender: CommandSender): Boolean {
        sender.sendMessage(
            """
                查看帮助：/market help
                出售主手上的物品：/market sale [价格]
                打开购买界面：/market buy
            """.trimIndent()
        )
        return true
    }

    private fun sale(sender: CommandSender, args: Array<out String>?): Boolean {
        val price = args?.get(1)?.toLongOrNull()
        return if (price == null) {
            sender.sendMessage("参数错误")
            false
        } else {
            val player = Bukkit.getPlayer(sender.name)
            if (player == null) {
                sender.sendMessage("玩家列表里没有找到本玩家")
                false
            } else {
                val itemStack = player.inventory.itemInMainHand
                if (itemStack.type == Material.AIR) {
                    sender.sendMessage("你的主手上没东西")
                    false
                } else {
                    Market.sale(player, itemStack, price)
                }
            }
        }
    }


    private fun buy(sender: CommandSender): Boolean {
        TODO("设计交易面板")
        return true
    }

}