package com.bnpp.assessment.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Standard Regex Blueprints for Financial/Registration Inputs
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}\\d{4}[A-Z]$");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z]{2}[a-zA-Z0-9_.]*$");

    /**
     * Validates Indian phone numbers (10 digits starting with 6-9).
     */
    public static boolean isPhoneValid(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates Permanent Account Number format (5 Letters, 4 Digits, 1 Letter).
     */
    public static boolean isPanValid(String pan) {
        if (pan == null) return false;
        return PAN_PATTERN.matcher(pan.trim().toUpperCase()).matches();
    }

    /**
     * Validates Aadhaar format constraint (12 consecutive numeric digits).
     */
    public static boolean isAadharValid(String aadhar) {
        if (aadhar == null) return false;
        return AADHAR_PATTERN.matcher(aadhar.trim()).matches();
    }

    /**
     * Validates email structure layout accuracy.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Matches constraint: "Username should contain atleast 2 characters in the beginning".
     */
    public static boolean isUsernameValid(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
}