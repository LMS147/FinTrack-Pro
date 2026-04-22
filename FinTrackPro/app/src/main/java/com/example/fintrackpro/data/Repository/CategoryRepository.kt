package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.CategoryDao
import com.example.fintrackpro.data.entity.Category
import kotlinx.coroutines.flow.Flow


class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun createCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    fun getCategoriesForUser(userId: Int): Flow<List<Category>> {
        return categoryDao.getCategoriesForUser(userId)
    }

    suspend fun getCategoryList(userId: Int): List<Category> {
        return categoryDao.getCategoryListOnce(userId)
    }

    suspend fun getCategoryById(categoryId: Int): Category? {
        return categoryDao.getCategoryById(categoryId)
    }
}