import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Category
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Tab
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.repository.Repository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel(
    private val repository: Repository
) : ViewModel() {

    val user: StateFlow<User?> = repository.currentUser

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _sumByType = MutableStateFlow<Map<String, Double>>(emptyMap())
    val sumByType: StateFlow<Map<String, Double>> = _sumByType

    private val _sumByCategory = MutableStateFlow<Map<String, Double>>(emptyMap())
    val sumByCategory: StateFlow<Map<String, Double>> = _sumByCategory

    private val _monthlyIncomeExpense = MutableStateFlow<Map<String, Map<String, Double>>>(emptyMap())
    val monthlyIncomeExpense: StateFlow<Map<String, Map<String, Double>>> = _monthlyIncomeExpense

    private val _currentMonthTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val currentMonthTransactions: StateFlow<List<Transaction>> = _currentMonthTransactions

    private val _previousMonthTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val previousMonthTransactions: StateFlow<List<Transaction>> = _previousMonthTransactions

    // New: mapped transaction lists with category/tab/saving pocket names instead of IDs
    private val _currentMonthTransactionsWithName = MutableStateFlow<List<Transaction>>(emptyList())
    val currentMonthTransactionsWithName: StateFlow<List<Transaction>> = _currentMonthTransactionsWithName

    private val _previousMonthTransactionsWithName = MutableStateFlow<List<Transaction>>(emptyList())
    val previousMonthTransactionsWithName: StateFlow<List<Transaction>> = _previousMonthTransactionsWithName

    // Existing flows collecting from repository
    val categoriesSharedFlow: SharedFlow<List<Category>> = repository.categoriesSharedFlow
    val savingPocketsGeneralFlow: SharedFlow<List<SavingPocket>> = repository.savingPocketsGeneralFlow
    val tabsSharedFlow: SharedFlow<List<Tab>> = repository.tabsGeneralFlow

    init {
        // Combine current/previous month transactions with categories, saving pockets, and tabs for mapping
        viewModelScope.launch {
            combine(
                _currentMonthTransactions,
                _previousMonthTransactions,
                categoriesSharedFlow,
                savingPocketsGeneralFlow,
                tabsSharedFlow
            ) { currTxs, prevTxs, categories, savingPockets, tabs ->

                // Build combined id -> name map from all entity lists
                val idToNameMap: Map<String, String> = (
                        categories.map { it.id to it.name } +
                                savingPockets.map { it.id to it.name } +
                                tabs.map { it.id to it.name }
                        ).toMap()

                // Map transactions replacing categoryId with the name from idToNameMap or fallback
                val currMapped = currTxs.map { tx ->
                    tx.copy(categoryId = idToNameMap[tx.categoryId] ?: tx.categoryId)
                }

                val prevMapped = prevTxs.map { tx ->
                    tx.copy(categoryId = idToNameMap[tx.categoryId] ?: tx.categoryId)
                }

                Pair(currMapped, prevMapped)
            }.collect { (currMapped, prevMapped) ->
                _currentMonthTransactionsWithName.value = currMapped
                _previousMonthTransactionsWithName.value = prevMapped
            }
        }

        // Collect transactions and update other aggregates, filtering current and previous month transactions
        viewModelScope.launch {
            repository.transactionSharedFlow.collect { txList ->
                _transactions.value = txList

                _sumByType.value = txList
                    .groupBy { it.type.name }
                    .mapValues { entry -> entry.value.sumOf { it.amount } }

                val incomeTypes = setOf(TransactionType.DEPOSIT, TransactionType.SAVINGS)
                val expenseTypes = setOf(TransactionType.WITHDRAWAL, TransactionType.TAB)
                _sumByCategory.value = mapOf(
                    "Income" to txList.filter { it.type in incomeTypes }.sumOf { it.amount },
                    "Expenses" to txList.filter { it.type in expenseTypes }.sumOf { it.amount }
                )

                val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val byMonth = txList
                    .filter { it.date != null }
                    .groupBy { tx -> dateFormat.format(tx.date!!.toDate()) }
                    .mapValues { (_, monthTxs) ->
                        mapOf(
                            "Income" to monthTxs.filter { it.type in incomeTypes }.sumOf { it.amount },
                            "Expenses" to monthTxs.filter { it.type in expenseTypes }.sumOf { it.amount }
                        )
                    }

                _monthlyIncomeExpense.value = byMonth

                Log.d("Transactions", "Updated with: $txList")
                Log.d("MonthlyIncomeExpense", "Updated: ${_monthlyIncomeExpense.value}")

                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH) // 0-based month
                val previousMonth: Int
                val previousYear: Int

                if (currentMonth == 0) {
                    previousMonth = 11
                    previousYear = currentYear - 1
                } else {
                    previousMonth = currentMonth - 1
                    previousYear = currentYear
                }

                fun isMonthAndYearMatch(ts: Timestamp?, year: Int, month: Int): Boolean {
                    ts ?: return false
                    val cal = Calendar.getInstance()
                    cal.time = ts.toDate()
                    return cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
                }

                val currMonthList = txList.filter { isMonthAndYearMatch(it.date, currentYear, currentMonth) }
                val prevMonthList = txList.filter { isMonthAndYearMatch(it.date, previousYear, previousMonth) }

                _currentMonthTransactions.value = currMonthList
                _previousMonthTransactions.value = prevMonthList

                Log.d("AnalyticsVM", "CurrentMonthTx: ${currMonthList.size} | PrevMonthTx: ${prevMonthList.size}")
            }
        }
    }
}
