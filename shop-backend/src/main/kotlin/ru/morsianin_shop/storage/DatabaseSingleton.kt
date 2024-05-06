package ru.morsianin_shop.storage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
    fun init(driver: String, url: String, user: String, pass: String, maxPool: Int = 6) {
        val config = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            password = pass
            maximumPoolSize = maxPool
            isReadOnly = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
        }

        val dataSource = HikariDataSource(config)

        Database.connect(
            datasource = dataSource,
            databaseConfig = DatabaseConfig {
            }
        )
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.configureStorage() {
    DatabaseSingleton.init(
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/shop",
        System.getenv("DB_USER") ?: "postgres",
        System.getenv("DB_PASSWORD") ?: "postgres");

    log.info("Storage initialized")
}