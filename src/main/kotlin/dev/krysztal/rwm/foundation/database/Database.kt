package dev.krysztal.rwm.foundation.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.krysztal.rwm.ReWorldMarketMain
import dev.krysztal.rwm.extender.getStringConfig
import dev.krysztal.rwm.foundation.market.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import javax.sql.DataSource
import kotlin.io.path.Path

/*
Account Table:
UserUUID    UserName    Balance
 */
object AccountTable : Table() {
    val userUUID: Column<String> = varchar("UserUUID", 36)
    val userName: Column<String> = varchar("UserName", 16)
    val balance: Column<Long> = long("Balance")

    override val primaryKey: PrimaryKey = PrimaryKey(userUUID)
}

/*
Table:
ItemUUID    Material    Amount    Price   UserUUID    UserName
 */
object ItemTable : Table() {
    val itemUUID: Column<String> = varchar("ItemUUID", 36)
    val material: Column<String> = text("Material")
    val amount: Column<Int> = integer("Amount")
    val price: Column<Long> = long("Price")
    val userUUID: Column<String> = varchar("UserUUID", 36)
    val userName: Column<String> = varchar("UserName", 16)
}


object Database {
    private val dataSource: DataSource
    private val hikariConfig: HikariConfig
    private val database: Database

    init {
        val databaseType = "storage.type".getStringConfig()
        val driver = when (databaseType) {
            "mysql", "mariadb" -> "com.mysql.cj.jdbc.Driver"
            else -> "org.h2.Driver"
        }

        val url = when (databaseType) {
            "mysql", "mariadb" ->
                "jdbc:$databaseType://" +
                        "${"storage.host".getStringConfig()}:" +
                        "${"storage.port".getStringConfig()}/" +
                        "${"storage.database".getStringConfig()}"

            else ->
                "jdbc:h2:${Path(ReWorldMarketMain.INSTANCE.dataFolder.absolutePath, "h2")}"
        }

        hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = "storage.username".getStringConfig()
            password = "storage.password".getStringConfig()
        }

        dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource)

        transaction {
            if (!AccountTable.exists()) {
                SchemaUtils.create(AccountTable)
                val players = Bukkit.getOfflinePlayers().asList()
                AccountTable.batchInsert(players) { player ->
                    this[AccountTable.userUUID] = player.uniqueId.toString()
                    this[AccountTable.userName] = player.name!!
                    this[AccountTable.balance] = 0
                }
            }
            if (!ItemTable.exists()) SchemaUtils.create(ItemTable)
        }
    }


    fun getPlayerBalance(player: Player): Long {
        return transaction {
            val account =
                AccountTable.select((AccountTable.userUUID eq player.uniqueId.toString()) or (AccountTable.userName eq player.name))
            return@transaction account.first()[AccountTable.balance]
        }
    }

    fun setPlayerBalance(player: Player, balance: Long) {
        transaction {
            AccountTable.update({ (AccountTable.userUUID eq player.uniqueId.toString()) or (AccountTable.userName eq player.name) })
            { account ->
                account[AccountTable.balance] = balance
            }
        }
    }


    fun getItemEntry(): MutableList<Item> {
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


    fun addItem(item: Item) {
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


    fun removeItem(item: Item) {
        transaction {
            ItemTable.deleteWhere { itemUUID eq item.uuid }
        }
    }
}