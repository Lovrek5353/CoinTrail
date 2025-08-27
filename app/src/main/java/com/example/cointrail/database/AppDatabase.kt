package com.example.cointrail.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [StockEntity::class], version=1
)


abstract class AppDatabase: RoomDatabase() {
    abstract fun stocksDao(): StocksDao
}
