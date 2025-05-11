package com.budgettracker.utils;

public class Constants {
    // Database related constants
    public static final String DATABASE_NAME = "budget_tracker_db";
    public static final int DATABASE_VERSION = 1;

    // Intent keys
    public static final String EXTRA_EXPENSE_ID = "expense_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_BUDGET_ID = "budget_id";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_START_DATE = "start_date";
    public static final String EXTRA_END_DATE = "end_date";

    // Request codes
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_STORAGE_PERMISSION = 101;

    // File provider authority
    public static final String FILE_PROVIDER_AUTHORITY = "com.budgettracker.fileprovider";

    // Shared Preferences keys
    public static final String PREF_CURRENCY_SYMBOL = "currency_symbol";
    public static final String PREF_DARK_MODE = "dark_mode";
    public static final String PREF_NOTIFICATION_ENABLED = "notification_enabled";
    public static final String PREF_BUDGET_REMINDER_TIME = "budget_reminder_time";

    // Default values
    public static final String DEFAULT_CURRENCY_SYMBOL = "$";
    public static final boolean DEFAULT_DARK_MODE = false;
    public static final boolean DEFAULT_NOTIFICATION_ENABLED = true;
    public static final String DEFAULT_BUDGET_REMINDER_TIME = "20:00";

    // Notification channels
    public static final String CHANNEL_ID_BUDGET_ALERTS = "budget_alerts";
    public static final String CHANNEL_NAME_BUDGET_ALERTS = "Budget Alerts";
    public static final String CHANNEL_DESCRIPTION_BUDGET_ALERTS = "Notifications for budget-related alerts";

    // Budget thresholds (in percentage)
    public static final int BUDGET_WARNING_THRESHOLD = 80;
    public static final int BUDGET_DANGER_THRESHOLD = 90;

    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String MONTH_YEAR_FORMAT = "yyyy-MM";
    public static final String DISPLAY_DATE_FORMAT = "MMM dd, yyyy";
    public static final String DISPLAY_TIME_FORMAT = "hh:mm a";
    public static final String DISPLAY_MONTH_YEAR_FORMAT = "MMMM yyyy";

    // Image related
    public static final int MAX_IMAGE_DIMENSION = 1024;
    public static final int IMAGE_COMPRESSION_QUALITY = 85;
    public static final String IMAGE_DIRECTORY = "BudgetTracker";
    public static final String IMAGE_PREFIX = "EXPENSE_";
    public static final String IMAGE_EXTENSION = ".jpg";

    // Error messages
    public static final String ERROR_INVALID_EMAIL = "Please enter a valid email address";
    public static final String ERROR_PASSWORD_TOO_SHORT = "Password must be at least 6 characters";
    public static final String ERROR_PASSWORDS_DONT_MATCH = "Passwords do not match";
    public static final String ERROR_EMAIL_TAKEN = "This email is already registered";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ERROR_FIELD_REQUIRED = "This field is required";
    public static final String ERROR_INVALID_AMOUNT = "Please enter a valid amount";
    public static final String ERROR_CATEGORY_EXISTS = "Category already exists";
    public static final String ERROR_MIN_GREATER_THAN_MAX = "Minimum spending cannot be greater than maximum";
    public static final String ERROR_CAMERA_UNAVAILABLE = "Camera is not available";
    public static final String ERROR_IMAGE_CAPTURE = "Failed to capture image";
    public static final String ERROR_FILE_CREATION = "Failed to create image file";

    // Success messages
    public static final String SUCCESS_REGISTRATION = "Registration successful";
    public static final String SUCCESS_EXPENSE_ADDED = "Expense added successfully";
    public static final String SUCCESS_CATEGORY_ADDED = "Category added successfully";
    public static final String SUCCESS_BUDGET_SET = "Budget set successfully";
    public static final String SUCCESS_SETTINGS_SAVED = "Settings saved successfully";

    // Menu item IDs
    public static final int MENU_ITEM_SETTINGS = 1;
    public static final int MENU_ITEM_ABOUT = 2;
    public static final int MENU_ITEM_LOGOUT = 3;

    // Chart related
    public static final int CHART_ANIMATION_DURATION = 1000;
    public static final float CHART_VALUE_TEXT_SIZE = 12f;
    public static final float CHART_LABEL_TEXT_SIZE = 14f;
    public static final int MAX_VISIBLE_VALUE_COUNT = 7;

    // RecyclerView related
    public static final int ITEMS_PER_PAGE = 20;
    public static final int LOAD_MORE_THRESHOLD = 5;

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
