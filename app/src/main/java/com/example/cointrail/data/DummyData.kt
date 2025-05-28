package com.example.cointrail.data

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

val dummySavingPocket = SavingPocket(
    id = "pocket123",
    name = "Vacation Fund",
    description = "Saving up for a summer trip to Europe.",
    userId = "user456",
    balance = 1500.0,
    targetAmount = 3000.0,
    targetDate = "2025-08-01"
)

val dummySavingPocket1 = SavingPocket(
    id = "pocket123",
    name = "Vacation Fund",
    description = "Saving up for a summer trip to Europe.",
    userId = "user456",
    balance = 1500.0,
    targetAmount = 3000.0,
    targetDate = "2025-08-01"
)

val dummySavingPocket2 = SavingPocket(
    id = "pocket124",
    name = "Emergency Fund",
    description = "Saving for unexpected expenses.",
    userId = "user456",
    balance = 2000.0,
    targetAmount = 5000.0,
    targetDate = "2025-12-31"
)

val dummySavingPocket3 = SavingPocket(
    id = "pocket125",
    name = "Car Fund",
    description = "Saving to buy a new car.",
    userId = "user456",
    balance = 1000.0,
    targetAmount = 10000.0,
    targetDate = "2026-06-01"
)


val savingPocketsList = listOf(dummySavingPocket1, dummySavingPocket2, dummySavingPocket3)

val dummyTransactions = listOf(
    Transaction("1", 10.0, "groceries", "2025-04-04", "Supermarket shopping", 0),
    Transaction("2", 18.0, "transport", "2025-04-04", "Bus pass", 0),
    Transaction("3", 21.0, "entertainment", "2025-04-05", "Movie ticket", 0),
    Transaction("4", 29.0, "utilities", "2025-04-05", "Electric bill", 0),
    Transaction("5", 32.0, "salary", "2025-04-06", "Freelance payment", 1),
    Transaction("6", 40.0, "coffee", "2025-04-06", "Coffee with friends", 0),
    Transaction("7", 13.0, "groceries", "2025-04-07", "Supermarket shopping", 0),
    Transaction("8", 21.0, "transport", "2025-04-07", "Bus pass", 0),
    Transaction("9", 24.0, "entertainment", "2025-04-08", "Movie ticket", 0),
    Transaction("10", 32.0, "utilities", "2025-04-08", "Electric bill", 0),
    Transaction("11", 30.0, "salary", "2025-04-09", "Freelance payment", 1),
    Transaction("12", 38.0, "coffee", "2025-04-09", "Coffee with friends", 0),
    Transaction("13", 11.0, "groceries", "2025-04-10", "Supermarket shopping", 0),
    Transaction("14", 19.0, "transport", "2025-04-10", "Bus pass", 0),
    Transaction("15", 22.0, "entertainment", "2025-04-11", "Movie ticket", 0),
    Transaction("16", 30.0, "utilities", "2025-04-11", "Electric bill", 0),
    Transaction("17", 33.0, "salary", "2025-04-12", "Freelance payment", 1),
    Transaction("18", 41.0, "coffee", "2025-04-12", "Coffee with friends", 0),
    Transaction("19", 14.0, "groceries", "2025-04-13", "Supermarket shopping", 0),
    Transaction("20", 22.0, "transport", "2025-04-13", "Bus pass", 0),
    Transaction("21", 20.0, "entertainment", "2025-04-14", "Movie ticket", 0),
    Transaction("22", 28.0, "utilities", "2025-04-14", "Electric bill", 0),
    Transaction("23", 31.0, "salary", "2025-04-15", "Freelance payment", 1),
    Transaction("24", 39.0, "coffee", "2025-04-15", "Coffee with friends", 0),
    Transaction("25", 12.0, "groceries", "2025-04-16", "Supermarket shopping", 0),
    Transaction("26", 20.0, "transport", "2025-04-16", "Bus pass", 0),
    Transaction("27", 23.0, "entertainment", "2025-04-17", "Movie ticket", 0),
    Transaction("28", 31.0, "utilities", "2025-04-17", "Electric bill", 0),
    Transaction("29", 34.0, "salary", "2025-04-18", "Freelance payment", 1),
    Transaction("30", 42.0, "coffee", "2025-04-18", "Coffee with friends", 0),
    Transaction("31", 10.0, "groceries", "2025-04-19", "Supermarket shopping", 0),
    Transaction("32", 18.0, "transport", "2025-04-19", "Bus pass", 0),
    Transaction("33", 21.0, "entertainment", "2025-04-20", "Movie ticket", 0),
    Transaction("34", 29.0, "utilities", "2025-04-20", "Electric bill", 0),
    Transaction("35", 32.0, "salary", "2025-04-21", "Freelance payment", 1),
    Transaction("36", 40.0, "coffee", "2025-04-21", "Coffee with friends", 0),
    Transaction("37", 13.0, "groceries", "2025-04-22", "Supermarket shopping", 0),
    Transaction("38", 21.0, "transport", "2025-04-22", "Bus pass", 0),
    Transaction("39", 24.0, "entertainment", "2025-04-23", "Movie ticket", 0),
    Transaction("40", 32.0, "utilities", "2025-04-23", "Electric bill", 0),
    Transaction("41", 30.0, "salary", "2025-04-24", "Freelance payment", 1),
    Transaction("42", 38.0, "coffee", "2025-04-24", "Coffee with friends", 0),
    Transaction("43", 11.0, "groceries", "2025-04-25", "Supermarket shopping", 0),
    Transaction("44", 19.0, "transport", "2025-04-25", "Bus pass", 0),
    Transaction("45", 22.0, "entertainment", "2025-04-26", "Movie ticket", 0),
    Transaction("46", 30.0, "utilities", "2025-04-26", "Electric bill", 0),
    Transaction("47", 33.0, "salary", "2025-04-27", "Freelance payment", 1),
    Transaction("48", 41.0, "coffee", "2025-04-27", "Coffee with friends", 0),
    Transaction("49", 14.0, "groceries", "2025-04-28", "Supermarket shopping", 0),
    Transaction("50", 22.0, "transport", "2025-04-28", "Bus pass", 0),
    Transaction("51", 20.0, "entertainment", "2025-04-29", "Movie ticket", 0),
    Transaction("35", 32.0, "salary", "2025-03-21", "Freelance payment", 1),
    Transaction("36", 40.0, "coffee", "2025-03-21", "Coffee with friends", 0),
    Transaction("37", 13.0, "groceries", "2025-02-22", "Supermarket shopping", 0),
    Transaction("38", 21.0, "transport", "2025-02-22", "Bus pass", 0),
    Transaction("39", 24.0, "entertainment", "2025-02-23", "Movie ticket", 0),
    Transaction("40", 32.0, "utilities", "2025-02-23", "Electric bill", 0),
    Transaction("41", 30.0, "salary", "2025-02-24", "Freelance payment", 1),
    Transaction("42", 38.0, "coffee", "2025-02-24", "Coffee with friends", 0),
    Transaction("43", 11.0, "groceries", "2025-02-25", "Supermarket shopping", 0),
    Transaction("44", 19.0, "transport", "2025-02-25", "Bus pass", 0),
    Transaction("45", 22.0, "entertainment", "2025-02-26", "Movie ticket", 0),
    Transaction("46", 30.0, "utilities", "2025-02-26", "Electric bill", 0),
    Transaction("47", 33.0, "salary", "2025-02-27", "Freelance payment", 1),
    Transaction("48", 41.0, "coffee", "2025-02-27", "Coffee with friends", 0),
    Transaction("49", 14.0, "groceries", "2025-03-28", "Supermarket shopping", 0),
    Transaction("50", 22.0, "transport", "2025-03-28", "Bus pass", 0),
    Transaction("51", 20.0, "entertainment", "2025-04-29", "Movie ticket", 0),
)

val dummyTransactions2 = listOf(
    Transaction("1", 10.0, "groceries", "2025-04-04", "Supermarket shopping", 0),
    Transaction("2", 18.0, "transport", "2025-04-04", "Bus pass", 0),
    Transaction("3", 21.0, "entertainment", "2025-04-05", "Movie ticket", 0),
    Transaction("4", 29.0, "utilities", "2025-04-05", "Electric bill", 0),
    Transaction("5", 32.0, "salary", "2025-04-06", "Freelance payment", 1)
)

val dummyTab = Tab(
    id = "tab123",
    name = "Car Loan",
    description = "Loan for purchasing a used car",
    userId = "user456",
    initialAmount = 15000.0,
    outstandingBalance = 8000.0,
    startDate = "2024-01-15",
    dueDate = "2026-01-15",
    monthlyPayment = 400.0,
    lender = "ABC Finance",
    status = "active"
)
