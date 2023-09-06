package dev.mcredbear.rwm.foundation.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.krysztal.rwm.ReWorldMarketMain
import dev.krysztal.rwm.foundation.database.AccountTable
import dev.krysztal.rwm.foundation.database.ItemTable
import dev.krysztal.rwm.foundation.database.LimitationTable
import dev.krysztal.rwm.foundation.market.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import java.util.*
import javax.sql.DataSource
import kotlin.io.path.Path

class DatabaseTest {
    private val dataSource: DataSource
    private val hikariConfig: HikariConfig
    private val database: Database

    private val storageType: String = ""
    private val storageHost: String = ""
    private val storagePort: String = ""
    private val storageDatabase: String = ""
    private val storageUsername: String = ""
    private val storagePassword: String = ""

    private val itemStack = ItemStack(Material.DIRT, 64)
    private val item = Item(UUID.randomUUID().toString(), itemStack, 114, null)

    private val playUUID: String = ""
    private val playerName: String = ""
    private val balance: Long = 114

    init {
        val databaseType = storageType
        val driver = when (databaseType) {
            "mysql", "mariadb" -> "com.mysql.cj.jdbc.Driver"
            "postgresql" -> "org.postgresql.Driver"
            else -> "org.h2.Driver"
        }

        val url = when (databaseType) {
            "mysql", "mariadb", "postgresql" ->
                "jdbc:$databaseType://" +
                        "${storageHost}:" +
                        "${storagePort}/" +
                        storageDatabase

            else ->
                "jdbc:h2:${Path(ReWorldMarketMain.INSTANCE.dataFolder.absolutePath, "h2")}"
        }

        hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = storageUsername
            password = storagePassword
        }

        dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource)

        // 检查数据表是否存在，没有则创建
        transaction {
            if (!AccountTable.exists()) {
                SchemaUtils.create(AccountTable)
            }
            if (!ItemTable.exists()) SchemaUtils.create(ItemTable)
            if (!LimitationTable.exists()) SchemaUtils.create(LimitationTable)
        }
    }

    @Test
    fun test(): Unit {
        addItem(item)
        println(getItemEntry())
        removeItem(item)
        println(getPlayerBalance(playUUID, playerName))
        setPlayerBalance(playUUID, playerName, balance)
    }

    // 获取玩家余额
    private fun getPlayerBalance(uuid: String, name: String): Long {
        return transaction {
            val account =
                AccountTable.select((AccountTable.userUUID eq uuid) or (AccountTable.userName eq name))
            return@transaction account.first()[AccountTable.balance]
        }
    }

    // 设置玩家余额
    private fun setPlayerBalance(uuid: String, name: String, balance: Long) {
        transaction {
            AccountTable.update({ (AccountTable.userUUID eq uuid) or (AccountTable.userName eq name) })
            { account ->
                account[AccountTable.balance] = balance
            }
        }
    }

    // 获取所有商品的清单
    private fun getItemEntry(): MutableList<Item> {
        return transaction {
            return@transaction ItemTable.selectAll().map { row ->
                val itemStack = ItemStack(Material.valueOf(row[ItemTable.material]), row[ItemTable.amount])
                val player =
                    Bukkit.getServer().getPlayer(UUID.fromString(row[ItemTable.userUUID])) ?: Bukkit.getServer()
                        .getPlayer(row[ItemTable.userName])
                Item(row[ItemTable.itemUUID], itemStack, row[ItemTable.price], player)
            }.toMutableList()
        }
    }

    // 添加商品
    private fun addItem(item: Item) {
        transaction {
            ItemTable.insert { row ->
                row[itemUUID] = item.uuid.toString()
                row[material] = item.itemStack.type.name
                row[amount] = item.itemStack.amount
                row[price] = item.price
                row[userUUID] = item.player?.uniqueId?.toString() ?: ""
                row[userName] = item.player?.name ?: ""
            }
        }
    }

    // 移除商品
    private fun removeItem(item: Item) {
        transaction {
            ItemTable.deleteWhere { itemUUID eq item.uuid }
        }
    }
}