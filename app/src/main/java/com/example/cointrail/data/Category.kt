package com.example.cointrail.data

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId var id: String="",
    var name: String = "",
    var description: String = "",
    var userId: String=""
)
