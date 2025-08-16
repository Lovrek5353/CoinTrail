package com.example.cointrail.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StocksDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun insertStock(stock: StockEntity)

    @Query("SELECT * FROM stocks")
    fun getStocks(symbol: String): MutableList<StockEntity>

    @Query("DELETE FROM stocks WHERE symbol =:symbol")
    fun removeStock(symbol: String)
}