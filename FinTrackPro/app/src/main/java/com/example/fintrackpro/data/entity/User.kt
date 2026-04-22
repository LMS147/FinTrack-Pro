package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for authentication and profile management.
 *  User Account Management.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val username: String,               // Unique login identifier
    val email: String,
    val passwordHash: String,           // Hashed password for security

    val displayName: String? = null,
    val photoUrl: String? = null,       // Profile picture URI

    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null,

    val defaultCurrency: String = "ZAR", // Default South African Rand
    val notificationsEnabled: Boolean = true,
    val biometricsEnabled: Boolean = false
)
