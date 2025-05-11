package com.budgettracker.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.util.Date;

@Entity(tableName = "expenses",
        foreignKeys = {
            @ForeignKey(entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId"),
            @ForeignKey(entity = Category.class,
                    parentColumns = "categoryId",
                    childColumns = "categoryId")
        })
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int expenseId;

    private int userId;
    private int categoryId;
    
    @NonNull
    private Date date;
    
    @NonNull
    private String time;
    
    @NonNull
    private String description;
    
    private double amount;
    
    private String photoUri;

    // Constructor
    public Expense(int userId, int categoryId, @NonNull Date date, @NonNull String time,
                  @NonNull String description, double amount, String photoUri) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.date = date;
        this.time = time;
        this.description = description;
        this.amount = amount;
        this.photoUri = photoUri;
    }

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
