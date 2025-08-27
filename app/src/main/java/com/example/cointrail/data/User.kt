package com.example.cointrail.data

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId var id: String? = null,
    var name: String = "",
    var email: String = "",
)
