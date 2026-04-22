package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-defined spending category.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    val userId: Int,                    // Foreign key to User
    val name: String,                   // e.g., "Food", "Transport"
    val iconResId: Int? = null,         // Resource ID for category icon
    val colorHex: String? = null,       // Hex color for UI representation
    val isDefault: Boolean = false      // Predefined system categories
)