package com.example.fintrackpro.ui.budget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.Budget
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val monthYear: String = getCurrentMonthYear()

    init {
        loadBudgetAndSpending()
    }

    private fun loadBudgetAndSpending() {
        viewModelScope.launch {
            // Get budget for the month
            val budget = budgetRepository.getBudgetForMonth(userId, monthYear)
            // Get total spending for the month
            val (startDate, endDate) = getMonthDateRange()
            val totalSpent = expenseRepository.getTotalExpensesForPeriod(userId, startDate, endDate)

            _uiState.value = BudgetUiState(
                budget = budget,
                totalSpent = totalSpent,
                monthYear = monthYear,
                isLoading = false
            )
        }
    }

    fun saveBudget(min: Double, max: Double) {
        viewModelScope.launch {
            budgetRepository.upsertBudget(userId, monthYear, min, max)
            loadBudgetAndSpending()  // Refresh display
        }
    }

    private fun getCurrentMonthYear(): String {
        val cal = Calendar.getInstance()
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
    }

    private fun getMonthDateRange(): Pair<Date, Date> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.time

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.time
        return Pair(start, end)
    }

    data class BudgetUiState(
        val budget: Budget? = null,
        val totalSpent: Double = 0.0,
        val monthYear: String = "",
        val isLoading: Boolean = true
    )
}