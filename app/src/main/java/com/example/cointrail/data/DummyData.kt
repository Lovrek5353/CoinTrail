package com.example.cointrail.data
import com.google.firebase.Timestamp
import java.util.Date

val dummyCategories = listOf(
    Category(id = "1", name = "Food", description = "Expenses for groceries, dining out, etc."),
    Category(id = "2", name = "Transport", description = "Public transport, fuel, taxi, etc."),
    Category(id = "3", name = "Entertainment", description = "Movies, subscriptions, and fun."),
    Category(id = "4", name = "Bills", description = "Electricity, water, internet, and rent."),
    Category(id = "5", name = "Shopping", description = "Clothing, gadgets, etc."),
    Category(id = "6", name = "Health", description = "Medical expenses and insurance."),
    Category(id = "7", name = "Travel", description = "Vacations, flights, hotels.")
)

val dummyUser = User(
    id = "user123",
    name = "John Doe",
    email = "john.doe@example.com"
)

//val dummySavingPocket = SavingPocket(
//    id = "pocket123",
//    name = "Vacation Fund",
//    description = "Saving up for a summer trip to Europe.",
//    userId = "user456",
//    balance = 1500.0,
//    targetAmount = 3000.0,
//    targetDate = "2025-08-01"
//)
//
//val dummySavingPocket1 = SavingPocket(
//    id = "pocket123",
//    name = "Vacation Fund",
//    description = "Saving up for a summer trip to Europe.",
//    userId = "user456",
//    balance = 1500.0,
//    targetAmount = 3000.0,
//    targetDate = "2025-08-01"
//)
//
//val dummySavingPocket2 = SavingPocket(
//    id = "pocket124",
//    name = "Emergency Fund",
//    description = "Saving for unexpected expenses.",
//    userId = "user456",
//    balance = 2000.0,
//    targetAmount = 5000.0,
//    targetDate = "2025-12-31"
//)
//
//val dummySavingPocket3 = SavingPocket(
//    id = "pocket125",
//    name = "Car Fund",
//    description = "Saving to buy a new car.",
//    userId = "user456",
//    balance = 1000.0,
//    targetAmount = 10000.0,
//    targetDate = "2026-06-01"
//)

//
//val savingPocketsList = listOf(dummySavingPocket1, dummySavingPocket2, dummySavingPocket3)


//val dummyTab = Tab(
//    id = "tab123",
//    name = "Car Loan",
//    description = "Loan for purchasing a used car",
//    userId = "user456",
//    initialAmount = 15000.0,
//    outstandingBalance = 8000.0,
//    startDate = "2024-01-15",
//    dueDate = "2026-01-15",
//    monthlyPayment = 400.0,
//    lender = "ABC Finance",
//    status = "active"
//)
