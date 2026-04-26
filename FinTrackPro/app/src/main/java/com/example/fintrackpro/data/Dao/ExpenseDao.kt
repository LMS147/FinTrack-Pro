package com.example.fintrackpro.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.fintrackpro.data.entity.Expense
import com.example.fintrackpro.data.entity.ExpensePhoto
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // View expenses with user-selectable date range
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId AND date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, createdAt DESC
    """)
    fun getExpensesBetweenDates(
        userId: Int,
        startDate: Date,
        endDate: Date
    ): Flow<List<Expense>>

    //Total spending per category within a date range
    @Query("""
        SELECT c.name, SUM(e.amount) as total 
        FROM expenses e 
        INNER JOIN categories c ON e.categoryId = c.categoryId 
        WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate AND e.isIncome = 0
        GROUP BY e.categoryId 
        ORDER BY total DESC
    """)
    suspend fun getCategorySpendingTotals(
        userId: Int,
        startDate: Date,
        endDate: Date
    ): List<CategorySpendingSummary>

    // order
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId 
        ORDER BY date DESC, createdAt DESC 
        LIMIT :limit
    """)
    fun getRecentExpenses(userId: Int, limit: Int = 5): Flow<List<Expense>>

    // Data class for the query result above
    data class CategorySpendingSummary(
        val name: String,
        val total: Double
    )

    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId")
    suspend fun getExpenseById(expenseId: Int): Expense?

    // For dashboard summary
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND isIncome = 0")
    fun getTotalExpenses(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND isIncome = 1")
    fun getTotalIncome(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate AND isIncome = 0")
    suspend fun getTotalExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Double?

    // ExpensePhoto management
    @Insert
    suspend fun insertPhoto(photo: ExpensePhoto): Long

    @Query("SELECT * FROM photos WHERE expenseId = :expenseId LIMIT 1")
    suspend fun getPhotoForExpense(expenseId: Int): ExpensePhoto?

    @Delete
    suspend fun deletePhoto(photo: ExpensePhoto)

}