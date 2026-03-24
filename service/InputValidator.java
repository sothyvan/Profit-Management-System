package service;

import errors.ValidationException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidator {
    public static String requireText(String value, String field) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " is required.");
        }
        return value.trim();
    }

    public static String requireDigits(String value, String field, int minLen, int maxLen)
            throws ValidationException {
        String v = requireText(value, field);
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c < '0' || c > '9') {
                throw new ValidationException(field + " must contain only digits.");
            }
        }
        if (v.length() < minLen || v.length() > maxLen) {
            throw new ValidationException(field + " must be " + minLen + "-" + maxLen + " digits.");
        }
        return v;
    }

    public static String requireLetters(String value, String field) throws ValidationException {
        String v = requireText(value, field);
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (!Character.isLetter(c) && c != ' ') {
                throw new ValidationException(field + " must contain letters only.");
            }
        }
        return v;
    }

    public static String requireUsername(String value, String field) throws ValidationException {
        String v = requireText(value, field);
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                throw new ValidationException(field + " must contain letters and numbers only.");
            }
        }
        return v;
    }

    public static double parsePositiveDouble(String value, String field) throws ValidationException {
        double num = parseDouble(value, field);
        if (num <= 0) {
            throw new ValidationException(field + " must be greater than 0.");
        }
        return num;
    }

    public static double parseNonNegativeDouble(String value, String field) throws ValidationException {
        double num = parseDouble(value, field);
        if (num < 0) {
            throw new ValidationException(field + " cannot be negative.");
        }
        return num;
    }

    private static double parseDouble(String value, String field) throws ValidationException {
        String v = requireText(value, field);
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException ex) {
            throw new ValidationException(field + " must be a number.");
        }
    }

    public static String parseDate(String value) throws ValidationException {
        String v = requireText(value, "Date");
        try {
            LocalDate parsed = LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE);
            return parsed.toString();
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Date must be YYYY-MM-DD.");
        }
    }

    public static String parseYearMonth(String value) throws ValidationException {
        String v = requireText(value, "Month");
        try {
            YearMonth parsed = YearMonth.parse(v);
            return parsed.toString();
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Month must be YYYY-MM.");
        }
    }

    public static String parseYear(String value) throws ValidationException {
        String v = requireText(value, "Year");
        if (v.length() != 4) {
            throw new ValidationException("Year must be YYYY.");
        }
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c < '0' || c > '9') {
                throw new ValidationException("Year must be numeric.");
            }
        }
        return v;
    }

    public static String normalizeRole(String value) throws ValidationException {
        String v = requireText(value, "Role").toLowerCase();
        if ("manager".equals(v)) {
            return "Manager";
        }
        if ("cashier".equals(v)) {
            return "Cashier";
        }
        throw new ValidationException("Role must be Manager or Cashier.");
    }

    public static void validateSalaryForRole(String role, double salary) throws ValidationException {
        if (salary < 0) {
            throw new ValidationException("Salary cannot be negative.");
        }
        if ("Manager".equals(role) && salary < 1000) {
            throw new ValidationException("Manager salary must be at least 1000.");
        }
        if ("Cashier".equals(role) && salary > 700) {
            throw new ValidationException("Cashier salary must be 700 or less.");
        }
    }
}
