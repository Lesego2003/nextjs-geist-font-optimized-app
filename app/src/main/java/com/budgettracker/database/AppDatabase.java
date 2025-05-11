package com.budgettracker.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(
    entities = {
        User.class,
        Category.class,
        Expense.class,
        Budget.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "budget_tracker_db";
    private static volatile AppDatabase instance;

    // DAOs
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ExpenseDao expenseDao();
    public abstract BudgetDao budgetDao();

    // Singleton pattern to get database instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        }
        return instance;
    }
}

// Date converter for Room
class DateConverter {
    @androidx.room.TypeConverter
    public static java.util.Date fromTimestamp(Long value) {
        return value == null ? null : new java.util.Date(value);
    }

    @androidx.room.TypeConverter
    public static Long dateToTimestamp(java.util.Date date) {
        return date == null ? null : date.getTime();
    }
}
