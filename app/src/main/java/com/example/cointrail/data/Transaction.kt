package com.example.cointrail.data

import com.example.cointrail.data.enums.TransactionType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Transaction (
    @DocumentId var id: String? = null,
    var amount: Double = 0.0,
    var categoryId: String="",  //can also be loan or savingPocket
    var date: Timestamp? = null,
    var description: String = "",
    var type: TransactionType = TransactionType.WITHDRAWAL,  //TransactionType Enum
    var userID: String=""
    )
