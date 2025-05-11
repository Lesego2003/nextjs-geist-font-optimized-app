package com.budgettracker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    // Get current date in yyyy-MM-dd format
    public static String getCurrentDate() {
        return DATE_FORMAT.format(new Date());
    }

    // Get current time in HH:mm format
    public static String getCurrentTime() {
        return TIME_FORMAT.format(new Date());
    }

    // Get current month-year in yyyy-MM format
    public static String getCurrentMonthYear() {
        return MONTH_YEAR_FORMAT.format(new Date());
    }

    // Convert Date object to string in yyyy-MM-dd format
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    // Convert string in yyyy-MM-dd format to Date object
    public static Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Format date for display (e.g., "Jan 01, 2024")
    public static String formatDateForDisplay(Date date) {
        return DISPLAY_DATE_FORMAT.format(date);
    }

    // Format time for display (e.g., "02:30 PM")
    public static String formatTimeForDisplay(String time) {
        try {
            Date timeDate = TIME_FORMAT.parse(time);
            return DISPLAY_TIME_FORMAT.format(timeDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    // Format month-year for display (e.g., "January 2024")
    public static String formatMonthYearForDisplay(String monthYear) {
        try {
            Date date = MONTH_YEAR_FORMAT.parse(monthYear);
            return DISPLAY_MONTH_YEAR_FORMAT.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return monthYear;
        }
    }

    // Get first day of current month
    public static Date getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Get last day of current month
    public static Date getLastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // Get first day of given month-year
    public static Date getFirstDayOfMonth(String monthYear) {
        try {
            Date date = MONTH_YEAR_FORMAT.parse(monthYear);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get last day of given month-year
    public static Date getLastDayOfMonth(String monthYear) {
        try {
            Date date = MONTH_YEAR_FORMAT.parse(monthYear);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            return cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Check if two dates are in the same month
    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    // Get previous month-year in yyyy-MM format
    public static String getPreviousMonthYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return MONTH_YEAR_FORMAT.format(cal.getTime());
    }

    // Get next month-year in yyyy-MM format
    public static String getNextMonthYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        return MONTH_YEAR_FORMAT.format(cal.getTime());
    }
}
