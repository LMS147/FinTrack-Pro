package com.example.fintrackpro.ui.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.ExpenseRepository

class ReportsViewModelFactory(
    private val context: Context,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            val database = FinTrackDatabase.getDatabase(context)
            val expenseRepo = ExpenseRepository(database.expenseDao())
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(expenseRepo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}