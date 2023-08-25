package dev.krysztal.rwm.foundation.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.krysztal.rwm.ReWorldMarketMain
import dev.krysztal.rwm.extender.getStringConfig
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource
import kotlin.io.path.Path

// @Suppress("Indentation")
object MarketDatabase {
    private val dataSource: DataSource
    private val hikariConfig: HikariConfig
    val database: Database

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
    }
}
