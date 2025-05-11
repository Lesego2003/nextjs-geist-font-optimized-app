package com.budgettracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insertCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId LIMIT 1")
    Category getCategoryById(int categoryId);

    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE categoryName = :categoryName)")
    boolean isCategoryExists(String categoryName);

    @Query("SELECT * FROM categories WHERE categoryName LIKE '%' || :search || '%'")
    List<Category> searchCategories(String search);

    @Query("DELETE FROM categories WHERE categoryId = :categoryId")
    void deleteCategoryById(int categoryId);
}
