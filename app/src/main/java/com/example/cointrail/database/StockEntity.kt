package com.example.cointrail.database

import androidx.core.util.TypedValueCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity (
    @PrimaryKey(autoGenerate = false)@ColumnInfo(name="symbol") val symbol: String,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="exchDisp") val exchDisp: String,
    @ColumnInfo(name="typeDispl") val typeDispl: String
)


