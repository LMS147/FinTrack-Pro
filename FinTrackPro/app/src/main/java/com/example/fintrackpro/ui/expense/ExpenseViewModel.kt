package com.example.fintrackpro.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.Expense
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // Filter parameters
    private val _startDate = MutableStateFlow(getDefaultStartDate())
    private val _endDate = MutableStateFlow(getDefaultEndDate())

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        // Combine start/end dates and fetch expenses
        viewModelScope.launch {
            combine(_startDate, _endDate) { start, end -> Pair(start, end) }
                .flatMapLatest { (start, end) ->
                    repository.getExpensesForPeriod(userId, start, end)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { expenses ->
                    _uiState.value = _uiState.value.copy(
                        expenses = expenses,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun setDateFilter(startDate: Date, endDate: Date) {
        _startDate.value = startDate
        _endDate.value = endDate
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete: ${e.message}")
            }
        }
    }

    private fun getDefaultStartDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun getDefaultEndDate(): Date = Calendar.getInstance().time

    data class ExpenseUiState(
        val expenses: List<Expense> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
}