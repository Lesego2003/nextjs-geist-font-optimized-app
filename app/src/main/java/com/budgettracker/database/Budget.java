package com.budgettracker.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "budgets",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId"))
public class Budget {
    @PrimaryKey(autoGenerate = true)
    private int budgetId;

    private int userId;
    
    @NonNull
    private String monthYear;
    
    private double minSpending;
    private double maxSpending;

    // Constructor
    public Budget(int userId, @NonNull String monthYear, double minSpending, double maxSpending) {
        this.userId = userId;
        this.monthYear = monthYear;
        this.minSpending = minSpending;
        this.maxSpending = maxSpending;
    }

    // Getters and Setters
    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(@NonNull String monthYear) {
        this.monthYear = monthYear;
    }

    public double getMinSpending() {
        return minSpending;
    }

    public void setMinSpending(double minSpending) {
        this.minSpending = minSpending;
    }

    public double getMaxSpending() {
        return maxSpending;
    }

    public void setMaxSpending(double maxSpending) {
        this.maxSpending = maxSpending;
    }
}
