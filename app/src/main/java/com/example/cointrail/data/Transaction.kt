package com.example.cointrail.data

import com.google.firebase.firestore.DocumentId

data class Transaction (
    @DocumentId var id: String? = null,
    var amount: Double = 0.0,
    var categoryId: String="",
    var date: String = "",
    var description: String = "",
    var type: Int=0        //TransactionType Enum
    )
