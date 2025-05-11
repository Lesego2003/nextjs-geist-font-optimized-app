package com.budgettracker.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.budgettracker.R;
import com.budgettracker.database.AppDatabase;
import com.budgettracker.database.Category;
import com.budgettracker.database.Expense;
import com.budgettracker.utils.Constants;
import com.budgettracker.utils.DateUtils;
import com.budgettracker.utils.ImageUtils;
import com.budgettracker.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddExpenseActivity extends AppCompatActivity {
    
    private TextInputLayout tilAmount, tilCategory, tilDate, tilTime, tilDescription;
    private TextInputEditText etAmount, etDate, etTime, etDescription;
    private AutoCompleteTextView spinnerCategory;
    private MaterialButton btnCamera, btnGallery, btnSave;
    private ImageView ivReceipt;
    
    private AppDatabase db;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    private Uri currentPhotoUri;
    private String selectedPhotoPath;
    private Map<String, Integer> categoryMap = new HashMap<>();
    private Calendar calendar = Calendar.getInstance();

    // Activity Result Launchers
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                handleCapturedPhoto();
            }
        }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                handleSelectedPhoto(result.getData().getData());
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize database and utilities
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executorService = Executors.newSingleThreadExecutor();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initViews();
        
        // Set default values
        setDefaultValues();
        
        // Load categories
        loadCategories();
        
        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        tilAmount = findViewById(R.id.tilAmount);
        tilCategory = findViewById(R.id.tilCategory);
        tilDate = findViewById(R.id.tilDate);
        tilTime = findViewById(R.id.tilTime);
        tilDescription = findViewById(R.id.tilDescription);
        
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        
        spinnerCategory = findViewById(R.id.spinnerCategory);
        
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnSave = findViewById(R.id.btnSave);
        
        ivReceipt = findViewById(R.id.ivReceipt);
    }

    private void setDefaultValues() {
        // Set current date and time
        etDate.setText(DateUtils.formatDateForDisplay(new Date()));
        etTime.setText(DateUtils.getCurrentTime());
    }

    private void loadCategories() {
        executorService.execute(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            
            for (Category category : categories) {
                categoryNames.add(category.getCategoryName());
                categoryMap.put(category.getCategoryName(), category.getCategoryId());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    categoryNames
                );
                spinnerCategory.setAdapter(adapter);
            });
        });
    }

    private void setClickListeners() {
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        
        btnCamera.setOnClickListener(v -> takePicture());
        btnGallery.setOnClickListener(v -> selectFromGallery());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etDate.setText(DateUtils.formatDateForDisplay(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        dialog.show();
    }

    private void takePicture() {
        try {
            File photoFile = ImageUtils.createImageFile(this);
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                Constants.FILE_PROVIDER_AUTHORITY,
                photoFile
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_camera_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void handleCapturedPhoto() {
        if (currentPhotoUri != null) {
            handleSelectedPhoto(currentPhotoUri);
        }
    }

    private void handleSelectedPhoto(Uri photoUri) {
        executorService.execute(() -> {
            String processedPath = ImageUtils.processImage(this, photoUri);
            if (processedPath != null) {
                selectedPhotoPath = processedPath;
                runOnUiThread(() -> {
                    ivReceipt.setVisibility(View.VISIBLE);
                    ivReceipt.setImageURI(Uri.fromFile(new File(processedPath)));
                });
            }
        });
    }

    private void saveExpense() {
        // Reset errors
        tilAmount.setError(null);
        tilCategory.setError(null);
        tilDescription.setError(null);

        // Get values
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(amountStr)) {
            tilAmount.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(category)) {
            tilCategory.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(description)) {
            tilDescription.setError(getString(R.string.error_field_required));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            tilAmount.setError(getString(R.string.error_invalid_amount));
            return;
        }

        // Get category ID
        Integer categoryId = categoryMap.get(category);
        if (categoryId == null) {
            tilCategory.setError(getString(R.string.error_invalid_category));
            return;
        }

        // Create expense object
        Expense expense = new Expense(
            sessionManager.getUserId(),
            categoryId,
            calendar.getTime(),
            etTime.getText().toString(),
            description,
            amount,
            selectedPhotoPath
        );

        // Save expense in background
        executorService.execute(() -> {
            long expenseId = db.expenseDao().insertExpense(expense);
            
            runOnUiThread(() -> {
                if (expenseId > 0) {
                    Toast.makeText(this, 
                        R.string.success_expense_added, 
                        Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this,
                        R.string.error_adding_expense,
                        Toast.LENGTH_SHORT).show();
                }
            });
        });
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
