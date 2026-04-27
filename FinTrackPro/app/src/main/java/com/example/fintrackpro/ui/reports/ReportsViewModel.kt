package com.example.fintrackpro.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.Dao.ExpenseDao.CategorySpendingSummary
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ReportsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private var startDate: Date = getDefaultStartDate()
    private var endDate: Date = Calendar.getInstance().time

    init {
        loadReport()
    }

    fun setDateRange(start: Date, end: Date) {
        startDate = start
        endDate = end
        loadReport()
    }

    fun refresh() {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val totals = expenseRepository.getCategorySpendingTotals(userId, startDate, endDate)
                _uiState.value = ReportsUiState(
                    categoryTotals = totals,
                    totalSpent = totals.sumOf { it.total },
                    startDate = startDate,
                    endDate = endDate,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getDefaultStartDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    data class ReportsUiState(
        val categoryTotals: List<CategorySpendingSummary> = emptyList(),
        val totalSpent: Double = 0.0,
        val startDate: Date? = null,
        val endDate: Date? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}