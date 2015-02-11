package is.valitor.lokaverkefni.oturgjold;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by eggert on 06/02/15.
 */
public class ValidatorTest {
    Validator v;

    @Before
    public void setup() {
        v = new Validator();
    }

    /**
     * Test cardholder name
     */
    @Test
    public void ValidateEmptyNameTest() {
        assertFalse(v.validateCardholderName(""));
    }

    @Test
    public void ValidateNonEmptyNameTest() {
        assertTrue(v.validateCardholderName("Eggert"));
    }

    /**
     * Test card number
     */
    @Test
    public void ValidateEmptyCardNumberTest() {
        assertEquals("", v.validateCardNumber(""));
    }

    @Test
    public void ValidateCardNumberContainsNonDigitTest() {
        assertEquals("", v.validateCardNumber("45873647586975ca"));
    }

    @Test
    public void ValidateCardNumberContainsOnlyDigitsTest() {
        assertEquals("visa", v.validateCardNumber("4587364758697534"));
    }

    @Test
    public void ValidateCardNumberContainsWhitespaceTest() {
        String clean = v.cleanupCardNumber("4587 3647 5869 7534");
        assertEquals("visa", v.validateCardNumber(clean));
    }

    /**
     * Test year
     */
    @Test
    public void ValidateEmptyYearTest() {
        assertFalse(v.validateYear(""));
    }

    @Test
    public void ValidateBogusYearTest() {
        assertFalse(v.validateYear("afiea"));
    }

    @Test
    public void ValidateShortYearTest() {
        assertFalse(v.validateYear("333"));
    }

    @Test
    public void ValidateLateYearTest() {
        assertFalse(v.validateYear("12333"));
    }

    @Test
    public void ValidateEarlyYearTest() {
        assertFalse(v.validateYear("1233"));
    }

    @Test
    public void ValidateValidYearTest() {
        String nextYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        assertTrue(v.validateYear(nextYear));
    }

    /**
     * Test month
     */
    @Test
    public void ValidateEmptyMonthTest() {
        assertFalse(v.validateMonth(""));
    }

    @Test
    public void ValidateBogusMonthTest() {
        assertFalse(v.validateMonth("afiea"));
    }

    @Test
    public void ValidateLowMonthTest() {
        assertFalse(v.validateMonth("-1"));
        assertFalse(v.validateMonth("0"));
    }

    @Test
    public void ValidateHighMonthTest() {
        assertFalse(v.validateMonth("13"));
        assertFalse(v.validateMonth("13728"));
    }

    @Test
    public void ValidateValidMonthTest() {
        assertTrue(v.validateMonth("1"));
        assertTrue(v.validateMonth("12"));
        assertTrue(v.validateMonth("5"));
    }

    /**
     * Test cvv
     */
    @Test
    public void ValidateEmptyCvvTest() {
        assertFalse(v.validateCvv(""));
    }

    @Test
    public void ValidateBogusCvvTest() {
        assertFalse(v.validateCvv("jfe82"));
    }

    @Test
    public void ValidateShortCvvTest() { assertFalse(v.validateCvv("82")); }

    @Test
    public void ValidateLongCvvTest() { assertFalse(v.validateCvv("827389")); }

    @Test
    public void ValidateValidCvvTest() {
        assertTrue(v.validateCvv("827"));
        assertTrue(v.validateCvv("1111"));
    }
}
