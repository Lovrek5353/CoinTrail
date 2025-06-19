package com.example.cointrail.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class SavingPocket(
    @DocumentId var id: String="",
    var name: String = "",
    var description: String = "",
    var userID: String = "",
    var balance: Double = 0.0,
    var targetAmount: Double = 0.0,
    var targetDate: Timestamp?=null,
)
