package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.ExpenseDao
import com.example.fintrackpro.data.entity.Expense
import com.example.fintrackpro.data.entity.ExpensePhoto
import kotlinx.coroutines.flow.*
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun addExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    fun getExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Flow<List<Expense>> =
        expenseDao.getExpensesBetweenDates(userId, startDate, endDate)

    suspend fun getExpenseById(expenseId: Int): Expense? = expenseDao.getExpenseById(expenseId)

    suspend fun addPhoto(photo: ExpensePhoto): Long = expenseDao.insertPhoto(photo)

    suspend fun getPhotoForExpense(expenseId: Int): ExpensePhoto? =
        expenseDao.getPhotoForExpense(expenseId)

    suspend fun deletePhoto(photo: ExpensePhoto) = expenseDao.deletePhoto(photo)

    // Other methods as before...
    fun getTotalIncome(userId: Int): Flow<Double?> = expenseDao.getTotalIncome(userId)
    fun getTotalExpenses(userId: Int): Flow<Double?> = expenseDao.getTotalExpenses(userId)
    fun getRecentExpenses(userId: Int, limit: Int = 5): Flow<List<Expense>> =
        expenseDao.getRecentExpenses(userId, limit)

    suspend fun getTotalExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Double {
        return expenseDao.getTotalExpensesForPeriod(userId, startDate, endDate) ?: 0.0
    }

    fun observeTotalExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Flow<Double> {
        return expenseDao.observeTotalExpensesForPeriod(userId, startDate, endDate).map { it ?: 0.0 }
    }

    fun getCategorySpendingTotals(
        userId: Int, startDate: Date, endDate: Date
    ): Flow<List<ExpenseDao.CategorySpendingSummary>> =
        expenseDao.getCategorySpendingTotals(userId, startDate, endDate)
}