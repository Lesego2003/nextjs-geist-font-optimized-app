package com.budgettracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    long insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY monthYear DESC")
    List<Budget> getAllBudgetsByUser(int userId);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear LIMIT 1")
    Budget getBudgetForMonth(int userId, String monthYear);

    @Query("SELECT EXISTS(SELECT 1 FROM budgets WHERE userId = :userId AND monthYear = :monthYear)")
    boolean hasBudgetForMonth(int userId, String monthYear);

    @Query("SELECT * FROM budgets WHERE budgetId = :budgetId LIMIT 1")
    Budget getBudgetById(int budgetId);

    @Query("DELETE FROM budgets WHERE userId = :userId")
    void deleteAllBudgetsByUser(int userId);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND " +
           "CAST(SUBSTR(monthYear, 1, 4) AS INTEGER) = :year")
    List<Budget> getBudgetsByYear(int userId, int year);

    @Query("SELECT AVG((maxSpending + minSpending) / 2) FROM budgets WHERE userId = :userId")
    double getAverageBudget(int userId);
}
