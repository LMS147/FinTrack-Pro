package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.ExpenseDao
import com.example.fintrackpro.data.entity.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    fun getExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Flow<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(userId, startDate, endDate)
    }

    suspend fun getCategoryTotals(userId: Int, startDate: Date, endDate: Date): List<ExpenseDao.CategorySpendingSummary> {
        return expenseDao.getCategorySpendingTotals(userId, startDate, endDate)
    }
}