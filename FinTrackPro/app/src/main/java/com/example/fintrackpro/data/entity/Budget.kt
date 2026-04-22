package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Monthly budget goals (min/max spending limits).
 */
@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val budgetId: Int = 0,

    val userId: Int,
    val monthYear: String,              // Format: "YYYY-MM" for uniqueness per month

    val minSpendingGoal: Double? = null,    // Minimum desired spend (optional)
    val maxSpendingGoal: Double,            // Maximum allowed spend (alert threshold)

    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)