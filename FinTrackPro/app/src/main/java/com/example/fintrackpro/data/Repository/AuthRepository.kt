package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.UserDao
import com.example.fintrackpro.data.entity.User

class AuthRepository(private val userDao: UserDao) {

    suspend fun login(username: String, passwordHash: String): User? {
        return userDao.login(username, passwordHash)
    }

    suspend fun register(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateLastLogin(userId: Int) {
        userDao.updateLastLogin(userId, System.currentTimeMillis())
    }
}