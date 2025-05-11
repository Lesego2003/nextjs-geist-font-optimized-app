package com.budgettracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.budgettracker.R;
import com.budgettracker.database.AppDatabase;
import com.budgettracker.database.User;
import com.budgettracker.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private View tvLogin;

    private AppDatabase db;
    private SessionManager sessionManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize database and session manager
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());
        
        tvLogin.setOnClickListener(v -> {
            finish(); // Return to login activity
        });
    }

    private void attemptRegistration() {
        // Reset errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Get values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            tilName.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_field_required));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_field_required));
            return;
        }

        if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_too_short));
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_passwords_dont_match));
            return;
        }

        // Attempt registration in background
        executorService.execute(() -> {
            // Check if email already exists
            boolean isEmailTaken = db.userDao().isEmailTaken(email);
            
            if (isEmailTaken) {
                runOnUiThread(() -> {
                    tilEmail.setError(getString(R.string.error_email_taken));
                });
                return;
            }

            // Create new user
            User newUser = new User(name, email, password);
            long userId = db.userDao().insertUser(newUser);

            runOnUiThread(() -> {
                if (userId > 0) {
                    // Create login session
                    sessionManager.createLoginSession((int) userId, name, email);

                    Toast.makeText(RegisterActivity.this,
                        R.string.registration_successful,
                        Toast.LENGTH_SHORT).show();

                    // Redirect to main activity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,
                        R.string.error_registration_failed,
                        Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
