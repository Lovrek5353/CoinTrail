import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel(
    var repository: Repository
) : ViewModel() {

    val user: StateFlow<User?> = repository.currentUser

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _sumByType = MutableStateFlow<Map<String, Double>>(emptyMap())
    val sumByType: StateFlow<Map<String, Double>> = _sumByType

    private val _sumByCategory = MutableStateFlow<Map<String, Double>>(emptyMap())
    val sumByCategory: StateFlow<Map<String, Double>> = _sumByCategory

    // New: Map<MonthLabel (e.g. "2024-07"), Map<"Income"/"Expenses", sum>>
    private val _monthlyIncomeExpense = MutableStateFlow<Map<String, Map<String, Double>>>(emptyMap())
    val monthlyIncomeExpense: StateFlow<Map<String, Map<String, Double>>> = _monthlyIncomeExpense

    init {
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

                // Group by Month!
                val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val byMonth = txList
                    .filter { it.date != null }
                    .groupBy { tx ->
                        dateFormat.format(tx.date!!.toDate())
                    }
                    .mapValues { (_, monthTxs) ->
                        mapOf(
                            "Income" to monthTxs.filter { it.type in incomeTypes }.sumOf { it.amount },
                            "Expenses" to monthTxs.filter { it.type in expenseTypes }.sumOf { it.amount }
                        )
                    }

                _monthlyIncomeExpense.value = byMonth

                Log.d("Transactions", "Updated with: $txList")
                Log.d("MonthlyIncomeExpense", "Updated: ${_monthlyIncomeExpense.value}")
            }
        }
    }
}
