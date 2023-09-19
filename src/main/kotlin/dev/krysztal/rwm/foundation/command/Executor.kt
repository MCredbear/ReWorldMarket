package dev.krysztal.rwm.foundation.command

import dev.krysztal.rwm.foundation.gui.MarketMenu
import dev.krysztal.rwm.foundation.market.Limitation
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
        return when (args?.getOrNull(0)) {
            "help" -> help(sender)
            "sale" -> sale(sender, args)
            "buy" -> buy(sender)
            "limitation" -> {
                return when (args.getOrNull(1)) {
                    "list" -> listLimitation(sender)
                    "add" -> addLimitation(sender, args)
                    "find" -> findLimitation(sender, args)
                    "set" -> setLimitation(sender, args)
                    "remove" -> removeLimitation(sender, args)
                    else -> {
                        sender.sendMessage("参数错误")
                        false
                    }
                }
            }

            else -> {
                sender.sendMessage("参数错误")
                false
            }
        }
    }

    // 打开帮助
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

    // 出售手上的物品
    private fun sale(sender: CommandSender, args: Array<out String>?): Boolean {
        val price = args?.getOrNull(1)?.toLongOrNull()
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

    // 打开购买面板
    private fun buy(sender: CommandSender): Boolean {
        MarketMenu.open(sender as Player)
        return true
    }

    private fun listLimitation(sender: CommandSender): Boolean {
        return Market.listLimitation(sender as Player)
    }

    // 子命令格式: [材质] {saleable=[yes/no]} {minPrice=[价格]} {maxPrice=[价格]} {recommendPrice=[价格]}
    private fun addLimitation(sender: CommandSender, args: Array<out String>?): Boolean {

        val subArgs = args?.sliceArray(2 until args.size)
        val materialArg = subArgs?.getOrNull(0) ?: run {
            sender.sendMessage("参数错误")
            return false
        }
        val material = try {
            Material.valueOf(materialArg.uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("参数错误")
            return false
        }
        val saleable =
            when (subArgs.find { arg -> arg.startsWith("saleable=", true) }?.split("=")?.getOrNull(1)?.lowercase()) {
                "yes" -> true
                "no" -> false
                null -> null
                else -> {
                    sender.sendMessage("参数错误")
                    return false
                }
            }
        val minPriceArg = subArgs.find { arg -> arg.startsWith("minPrice=", true) }?.split("=")?.getOrNull(1)
        val minPrice = if (minPriceArg == null) {
            null
        } else {
            minPriceArg.toLongOrNull() ?: run {
                sender.sendMessage("参数错误")
                return false
            }
        }

        val maxPriceArg = subArgs.find { arg -> arg.startsWith("maxPrice=", true) }?.split("=")?.getOrNull(1)
        val maxPrice = if (maxPriceArg == null) {
            null
        } else {
            maxPriceArg.toLongOrNull() ?: run {
                sender.sendMessage("参数错误")
                return false
            }
        }
        val recommendPriceArg =
            subArgs.find { arg -> arg.startsWith("recommendPrice=", true) }?.split("=")?.getOrNull(1)
        val recommendPrice = if (recommendPriceArg == null) {
            null
        } else {
            recommendPriceArg.toLongOrNull() ?: run {
                sender.sendMessage("参数错误")
                return false
            }
        }

        val limitation = Limitation(material, saleable, minPrice, maxPrice, recommendPrice)
        return Market.addLimitation(sender as Player, limitation)
    }

    private fun findLimitation(sender: CommandSender, args: Array<out String>?): Boolean {
        val subArgs = args?.sliceArray(2 until args.size)
        val materialArg = subArgs?.getOrNull(0) ?: run {
            sender.sendMessage("参数错误")
            return false
        }
        val material = try {
            Material.valueOf(materialArg.uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("参数错误")
            return false
        }
        return Market.findLimitation(sender as Player, material)
    }

    private fun setLimitation(sender: CommandSender, args: Array<out String>?): Boolean {

        val subArgs = args?.sliceArray(2 until args.size)
        val materialArg = subArgs?.getOrNull(0) ?: run {
            sender.sendMessage("参数错误")
            return false
        }
        val material = try {
            Material.valueOf(materialArg.uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("参数错误")
            return false
        }
        val saleable =
            when (subArgs.find { arg -> arg.startsWith("saleable=", true) }?.split("=")?.getOrNull(1)?.lowercase()) {
                "yes" -> true
                "no" -> false
                null -> null
                else -> {
                    sender.sendMessage("参数错误")
                    return false
                }
            }
        val minPriceArg = subArgs.find { arg -> arg.startsWith("minPrice=", true) }?.split("=")?.getOrNull(1)
        val minPrice = if (minPriceArg == null) {
            null
        } else {
            minPriceArg.toLongOrNull() ?: run {
                sender.sendMessage("参数错误")
                return false
            }
        }

        val maxPriceArg = subArgs.find { arg -> arg.startsWith("maxPrice=", true) }?.split("=")?.getOrNull(1)
        val maxPrice = if (maxPriceArg == null) {
            null
        } else {
            maxPriceArg.toLongOrNull() ?: run {
                sender.sendMessage("参数错误")
                return false
            }
        }
        val recommendPriceArg =
            subArgs.find { arg -> arg.startsWith("recommendPrice=", true) }?.split("=")?.getOrNull(1)
        val recommendPrice = if (recommendPriceArg == null) {
            null
        } else {
            recommendPriceArg.toLongOrNull() ?: run {
                sender.sendMessage("参数错误")
                return false
            }
        }

        val limitation = Limitation(material, saleable, minPrice, maxPrice, recommendPrice)
        return Market.setLimitation(sender as Player, limitation)
    }

    private fun removeLimitation(sender: CommandSender, args: Array<out String>?): Boolean {
        val subArgs = args?.sliceArray(2 until args.size)
        val materialArg = subArgs?.getOrNull(0) ?: run {
            sender.sendMessage("参数错误")
            return false
        }
        if (materialArg.equals("all", true)) return Market.removeAllLimitation(sender as Player)
        val material = try {
            Material.valueOf(materialArg.uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("参数错误")
            return false
        }
        return Market.removeLimitation(sender as Player, material)
    }
}
