package com.budgettracker.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budgettracker.R;
import com.budgettracker.adapters.CategoryAdapter;
import com.budgettracker.database.AppDatabase;
import com.budgettracker.database.Category;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryActivity extends AppCompatActivity implements CategoryAdapter.CategoryClickListener {
    
    private TextInputLayout tilCategoryName;
    private TextInputEditText etCategoryName;
    private MaterialButton btnAddCategory;
    private RecyclerView rvCategories;
    private View layoutEmpty;
    
    private AppDatabase db;
    private CategoryAdapter adapter;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialize database and executor
        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initViews();
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Set click listeners
        setClickListeners();

        // Load categories
        loadCategories();
    }

    private void initViews() {
        tilCategoryName = findViewById(R.id.tilCategoryName);
        etCategoryName = findViewById(R.id.etCategoryName);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        rvCategories = findViewById(R.id.rvCategories);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(this, this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);
    }

    private void setClickListeners() {
        btnAddCategory.setOnClickListener(v -> attemptAddCategory());
    }

    private void attemptAddCategory() {
        // Reset error
        tilCategoryName.setError(null);

        // Get category name
        String categoryName = etCategoryName.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(categoryName)) {
            tilCategoryName.setError(getString(R.string.error_field_required));
            return;
        }

        // Add category in background
        executorService.execute(() -> {
            // Check if category already exists
            boolean exists = db.categoryDao().isCategoryExists(categoryName);
            
            if (exists) {
                runOnUiThread(() -> {
                    tilCategoryName.setError(getString(R.string.error_category_exists));
                });
                return;
            }

            // Create new category
            Category category = new Category(categoryName);
            long categoryId = db.categoryDao().insertCategory(category);

            runOnUiThread(() -> {
                if (categoryId > 0) {
                    // Clear input
                    etCategoryName.setText("");
                    
                    // Show success message
                    Toast.makeText(this, 
                        R.string.success_category_added, 
                        Toast.LENGTH_SHORT).show();

                    // Reload categories
                    loadCategories();
                } else {
                    Toast.makeText(this,
                        R.string.error_adding_category,
                        Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadCategories() {
        executorService.execute(() -> {
            // Get all categories
            List<Category> categories = db.categoryDao().getAllCategories();
            
            // Get expense count for each category
            Map<Integer, Integer> expenseCountMap = new HashMap<>();
            for (Category category : categories) {
                int count = db.expenseDao()
                    .getExpensesByCategory(0, category.getCategoryId()).size();
                expenseCountMap.put(category.getCategoryId(), count);
            }

            runOnUiThread(() -> {
                adapter.submitList(categories);
                adapter.setExpenseCount(expenseCountMap);
                
                // Toggle empty state visibility
                layoutEmpty.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
                rvCategories.setVisibility(categories.isEmpty() ? View.GONE : View.VISIBLE);
            });
        });
    }

    @Override
    public void onDeleteCategory(Category category) {
        executorService.execute(() -> {
            db.categoryDao().deleteCategory(category);
            runOnUiThread(() -> {
                Toast.makeText(this, 
                    getString(R.string.category_deleted, category.getCategoryName()),
                    Toast.LENGTH_SHORT).show();
                loadCategories();
            });
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        // Handle category click (if needed)
        // For example, you might want to show category details or edit the category
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
