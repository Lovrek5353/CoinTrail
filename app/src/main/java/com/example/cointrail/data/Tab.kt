package com.example.cointrail.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Tab(
    @DocumentId var id: String = "",
    var name: String = "",
    var description: String = "",
    var userId: String = "",
    var initialAmount: Double = 0.0,            // Original loan amount
    var outstandingBalance: Double = 0.0,   // Remaining amount to be repaid
    var interestRate: Double = 0.0,         // Annual interest rate (as a percentage, e.g., 5.5)
    var startDate: Timestamp? =null,             // When the loan started (ISO date string)
    var dueDate: Timestamp? =null,               // When the loan should be fully repaid
    var monthlyPayment: Double = 0.0,       // Regular payment amount
    var lender: String = "",                // Who provided the loan
    var status: String = "active"           // e.g., "active", "paid", "overdue"
)
