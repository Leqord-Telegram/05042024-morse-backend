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

object DatabaseStorage {
    private var database: Database? = null

    fun init(driver: String, url: String, user: String, pass: String) {
        val config = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            password = pass
            maximumPoolSize = 32
            isReadOnly = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
        }

        val dataSource = HikariDataSource(config)

        database = Database.connect(
            datasource = dataSource,
            databaseConfig = DatabaseConfig {
            }
        )

        transaction {
            SchemaUtils.create(StoredUsers)
            SchemaUtils.create(StoredImages)
            SchemaUtils.create(StoredCategories)
            SchemaUtils.create(StoredProducts)
            SchemaUtils.create(StoredProductImages)
            SchemaUtils.create(StoredOrders)
            SchemaUtils.create(StoredOrderItems)
            SchemaUtils.create(StoredUserCartItems)
            SchemaUtils.create(StoredUserPrivileges)
            SchemaUtils.create(StoredLabels)
            SchemaUtils.create(StoredProductLabels)
            SchemaUtils.create(StoredKVs)
            SchemaUtils.create(StoredProductCategories)
            SchemaUtils.create(StoredUserProductNotifications)
        }
    }

    fun getDatabase(): Database {
        if  (database==null) {
            throw IllegalStateException("Database is not initialized")
        }
        return database!!
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db = getDatabase()) { block() }
}

fun Application.configureStorage() {
    val driver = "org.postgresql.Driver"
    val db = System.getenv("DB_NAME")!!
    val port = System.getenv("DB_PORT")?: "5432"
    val host = System.getenv("DB_HOST")?: "localhost"
    val url = "jdbc:postgresql://$host:$port/$db"
    val user = System.getenv("DB_USER")!!
    val password = System.getenv("DB_PASSWORD")!!

    log.info("Connecting to: $url user: $user driver: $driver")

    DatabaseStorage.init(
        driver=driver,
        url=url,
        user=user,
        pass=password
    )

    log.info("Storage initialized")
}