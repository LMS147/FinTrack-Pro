package com.example.fintrackpro.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.Dao.ExpenseDao.CategorySpendingSummary
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ReportsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _startDate = MutableStateFlow(getDefaultStartDate())
    private val _endDate = MutableStateFlow(getDefaultEndDate())

    init {
        observeReport()
    }

    fun setDateRange(start: Date, end: Date) {
        _startDate.value = start
        _endDate.value = end
    }

    fun refresh() {
        // Update end date to end of today to catch new entries
        _endDate.value = getDefaultEndDate()
    }

    private fun getDefaultEndDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    private fun observeReport() {
        viewModelScope.launch {
            combine(
                combine(_startDate, _endDate) { start, end -> Pair(start, end) }
                    .flatMapLatest { (start, end) ->
                        expenseRepository.getCategorySpendingTotals(userId, start, end)
                            .map { Triple(it, start, end) }
                    },
                authRepository.getUserFlow(userId)
            ) { (totals, start, end), user ->
                ReportsUiState(
                    categoryTotals = totals,
                    totalSpent = totals.sumOf { it.total },
                    startDate = start,
                    endDate = end,
                    currency = user?.defaultCurrency ?: "ZAR",
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }.collect { state ->
                _uiState.value = state
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
        val currency: String = "ZAR",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}