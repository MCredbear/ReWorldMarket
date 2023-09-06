package dev.krysztal.rwm.foundation.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.krysztal.rwm.ReWorldMarketMain
import dev.krysztal.rwm.extender.getStringConfig
import dev.krysztal.rwm.foundation.market.Item
import dev.krysztal.rwm.foundation.market.Limitation
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

    override val primaryKey: PrimaryKey = PrimaryKey(itemUUID)
}

/*
Table:
Material    Saleable    MinPrice    MaxPrice    RecommendPrice
 */
object LimitationTable : Table() {
    val material: Column<String> = text("Material")
    val saleable: Column<Boolean?> = bool("Saleable").nullable()
    val minPrice: Column<Long?> = long("MinPrice").nullable()
    val maxPrice: Column<Long?> = long("MaxPrice").nullable()
    val recommendPrice: Column<Long?> = long("RecommendPrice").nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(material)
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
            "mysql", "mariadb", "postgresql" ->
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

        // 检查数据表是否存在，没有则创建
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
            if (!LimitationTable.exists()) SchemaUtils.create(LimitationTable)
        }
    }


    // 获取玩家余额
    fun getPlayerBalance(player: Player): Long {
        return transaction {
            val account =
                AccountTable.select((AccountTable.userUUID eq player.uniqueId.toString()) or (AccountTable.userName eq player.name))
            return@transaction account.first()[AccountTable.balance]
        }
    }

    // 设置玩家余额
    fun setPlayerBalance(player: Player, balance: Long) {
        transaction {
            AccountTable.update({ (AccountTable.userUUID eq player.uniqueId.toString()) or (AccountTable.userName eq player.name) })
            { account ->
                account[AccountTable.balance] = balance
            }
        }
    }

    // 获取所有商品的清单
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

    // 添加商品
    fun addItem(item: Item) {
        transaction {
            ItemTable.insert { row ->
                row[itemUUID] = item.uuid
                row[material] = item.itemStack.type.name
                row[amount] = item.itemStack.amount
                row[price] = item.price
                row[userUUID] = item.player?.uniqueId?.toString() ?: ""
                row[userName] = item.player?.name ?: ""
            }
        }
    }

    // 移除商品
    fun removeItem(item: Item) {
        transaction {
            ItemTable.deleteWhere { itemUUID eq item.uuid }
        }
    }

    // 添加限制
    fun addLimitation(limitation: Limitation) {
        transaction {
            LimitationTable.insert { row ->
                row[material] = limitation.material.toString()
                row[saleable] = limitation.saleable
                row[minPrice] = limitation.minPrice
                row[maxPrice] = limitation.maxPrice
                row[recommendPrice] = limitation.recommendPrice
            }
        }
    }

    // 查找限制
    fun findLimitation(material: Material): Limitation? {
        return transaction {
            val limitation = LimitationTable.select(LimitationTable.material eq material.toString())
            return@transaction if (limitation.empty()) null else Limitation(
                Material.valueOf(limitation.first()[LimitationTable.material]),
                limitation.first()[LimitationTable.saleable],
                limitation.first()[LimitationTable.minPrice],
                limitation.first()[LimitationTable.maxPrice],
                limitation.first()[LimitationTable.recommendPrice]
            )
        }
    }

    // 修改限制
    fun setLimitation(limitation: Limitation) {
        transaction {
            LimitationTable.update({ LimitationTable.material eq limitation.material.toString() }) { preLimitation ->
                preLimitation[saleable] = limitation.saleable
                preLimitation[minPrice] = limitation.minPrice
                preLimitation[maxPrice] = limitation.maxPrice
                preLimitation[recommendPrice] = limitation.recommendPrice
            }
        }
    }

    // 移除限制
    fun removeLimitation(material: Material) {
        transaction {
            LimitationTable.deleteWhere { LimitationTable.material eq material.toString() }
        }
    }

    // 移除所有限制
    fun removeAllLimitation() {
        transaction {
            LimitationTable.deleteAll()
        }
    }
}