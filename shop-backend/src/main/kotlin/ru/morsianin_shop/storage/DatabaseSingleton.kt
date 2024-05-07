package ru.morsianin_shop.storage

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseSingleton {
    private var database: Database? = null;

    fun init(driver: String, url: String, user: String, pass: String) {
        database = Database.connect(url, driver, user, pass)
    }

    fun getDatabase(): Database {
        if  (database==null) {
            throw IllegalStateException("Database is not initialized")
        }
        return database!!
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.configureStorage() {
    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://localhost:5432/shop"
    val user = System.getenv("DB_USER") ?: "postgres"
    val password = System.getenv("DB_PASSWORD") ?: "postgres"

    log.info("Connecting to: $url user: $user driver: $driver")

    DatabaseSingleton.init(
        driver=driver,
        url=url,
        user=user,
        pass=password
    )

    log.info("Storage initialized")
}