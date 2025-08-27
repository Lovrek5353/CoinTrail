package com.example.cointrail.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StocksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Query("SELECT * FROM stocks")
    fun getStocks(): Flow<List<StockEntity>>

    @Query("DELETE FROM stocks WHERE symbol =:symbol")
    suspend fun removeStock(symbol: String)
}