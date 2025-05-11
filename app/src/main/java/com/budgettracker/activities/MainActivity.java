package com.budgettracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.budgettracker.R;
import com.budgettracker.database.AppDatabase;
import com.budgettracker.database.Budget;
import com.budgettracker.database.Expense;
import com.budgettracker.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView tvTotalExpenses;
    private LinearProgressIndicator progressBudget;
    private MaterialCardView cardAddExpense, cardViewExpenses, cardCategories, 
                           cardBudget, cardReports;
    
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database and session manager
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executorService = Executors.newSingleThreadExecutor();
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        initViews();
        
        // Set click listeners
        setClickListeners();

        // Load dashboard data
        loadDashboardData();
    }

    private void initViews() {
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        progressBudget = findViewById(R.id.progressBudget);
        cardAddExpense = findViewById(R.id.cardAddExpense);
        cardViewExpenses = findViewById(R.id.cardViewExpenses);
        cardCategories = findViewById(R.id.cardCategories);
        cardBudget = findViewById(R.id.cardBudget);
        cardReports = findViewById(R.id.cardReports);
    }

    private void setClickListeners() {
        cardAddExpense.setOnClickListener(v -> 
            startActivity(new Intent(this, AddExpenseActivity.class)));
            
        cardViewExpenses.setOnClickListener(v -> 
            startActivity(new Intent(this, ExpenseListActivity.class)));
            
        cardCategories.setOnClickListener(v -> 
            startActivity(new Intent(this, CategoryActivity.class)));
            
        cardBudget.setOnClickListener(v -> 
            startActivity(new Intent(this, BudgetActivity.class)));
            
        cardReports.setOnClickListener(v -> 
            startActivity(new Intent(this, ReportActivity.class)));
    }

    private void loadDashboardData() {
        executorService.execute(() -> {
            int userId = sessionManager.getUserId();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            java.util.Date startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            java.util.Date endDate = cal.getTime();

            // Get current month's expenses
            double totalExpenses = db.expenseDao()
                .getTotalExpensesByDateRange(userId, startDate, endDate);

            // Get current month's budget
            String monthYear = String.format(Locale.getDefault(), "%d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
            Budget budget = db.budgetDao().getBudgetForMonth(userId, monthYear);

            runOnUiThread(() -> updateDashboard(totalExpenses, budget));
        });
    }

    private void updateDashboard(double totalExpenses, Budget budget) {
        // Update total expenses
        tvTotalExpenses.setText(currencyFormatter.format(totalExpenses));

        // Update progress bar if budget exists
        if (budget != null) {
            double maxBudget = budget.getMaxSpending();
            int progress = (int) ((totalExpenses / maxBudget) * 100);
            progressBudget.setProgress(Math.min(progress, 100));
            
            // Change progress color based on spending
            if (progress >= 90) {
                progressBudget.setIndicatorColor(getColor(R.color.red));
            } else if (progress >= 75) {
                progressBudget.setIndicatorColor(getColor(R.color.orange));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData(); // Refresh dashboard data
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
