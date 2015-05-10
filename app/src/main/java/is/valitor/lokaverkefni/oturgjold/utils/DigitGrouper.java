package is.valitor.lokaverkefni.oturgjold.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

/**
 * Created by dasve_000 on 5/5/2015.
 */
public class DigitGrouper implements TextWatcher {

    public static final char space = ' ';

    private int digitModulus;
    private int digitBlock;

    public DigitGrouper(int size) {
        this.digitModulus = size + 1;
        this.digitBlock = size;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() > 0 && (s.length() % digitModulus) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (space == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % digitModulus) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length < digitBlock) {
                s.insert(s.length() - 1, String.valueOf(space));
            }
        }
    }
}
