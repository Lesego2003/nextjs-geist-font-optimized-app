package com.budgettracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.Date;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    long insertExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    List<Expense> getAllExpensesByUser(int userId);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Expense> getExpensesByDateRange(int userId, Date startDate, Date endDate);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    List<Expense> getExpensesByCategory(int userId, int categoryId);

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    double getTotalExpensesByDateRange(int userId, Date startDate, Date endDate);

    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    List<CategoryExpenseSum> getExpenseSumByCategory(int userId, Date startDate, Date endDate);

    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId LIMIT 1")
    Expense getExpenseById(int expenseId);

    @Query("DELETE FROM expenses WHERE userId = :userId")
    void deleteAllExpensesByUser(int userId);

    // Static class for category-wise expense sum
    static class CategoryExpenseSum {
        public int categoryId;
        public double total;
    }
}
