package is.valitor.lokaverkefni.oturgjold.utils;

import java.util.Calendar;

/**
 * Created by eggert on 06/02/15.
 *
 * Helper for validating input
 */
public class Validator {
    /**
     * Validate Cardholder name
     * Only checks for non-empty string.
     */
    public boolean validateCardholderName(String name) {
        if (name.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Remove allowed characters from card number
     */
    public String cleanupCardNumber(String number) {
        String allowedChars = "[\\s-]+";

        return number.replaceAll(allowedChars, "");
    }

    /**
     * Validate Card number
     *
     * Should call cleanCardNumber first to strip allowed chars
     *
     * Returns a string containing the card type or empty string for invalid
     * Card types supported:
     *  - visa
     *  - mastercard
     *  - amex
     */
    public String validateCardNumber(String number) {
        String visa = "^4[0-9]{12}(?:[0-9]{3})?$";
        String mc = "^5[1-5][0-9]{14}$";
        String amex = "^3[47][0-9]{13}$";

        String clean = cleanupCardNumber(number);

        if(clean.matches(visa)){
            return "visa";
        }
        if(clean.matches(mc)){
            return "mastercard";
        }
        if(clean.matches(amex)){
            return "amex";
        }
        return "";
    }

    /**
     * Validate year
     *
     * Checks for 4 digits and after current year
     */
    public boolean validateYear(String year) {
        String match = "[0-9]{4}";

        if (!year.matches(match)) {
            return false;
        }

        // No earlier year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int suppliedYear = Integer.decode(year);
        if (suppliedYear < currentYear) {
            return false;
        }
        return true;
    }

    /**
     * Validate month
     *
     * Checks for int value 1 - 12 inclusive
     */
    public boolean validateMonth(String month) {
        String match = "[0-9]+";

        if (!month.matches(match)) {
            return false;
        }
        int suppliedMonth = Integer.decode(month);
        if (suppliedMonth < 1 || suppliedMonth > 12) {
            return false;
        }
        return true;
    }

    /**
     * Validate security number CVV / CVC
     *
     * Checks for a 3 or 4 digit string
     */
    public boolean validateCvv(String cvv) {
        String match = "[0-9]{3,4}";

        if (!cvv.matches(match)) {
            return false;
        }
        return true;
    }

}
