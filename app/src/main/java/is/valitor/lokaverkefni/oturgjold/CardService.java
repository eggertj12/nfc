package is.valitor.lokaverkefni.oturgjold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;

import is.valitor.lokaverkefni.oturgjold.repository.Card;
import is.valitor.lokaverkefni.oturgjold.repository.Repository;
import is.valitor.lokaverkefni.oturgjold.repository.Token;
import is.valitor.lokaverkefni.oturgjold.service.GetTokenTask;
import is.valitor.lokaverkefni.oturgjold.utils.NetworkUtil;


/**
 * Handles receiving APDU messages the OS forwards from NFC reader
 */
public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    // AID for our payment service.
    private static final String OTURGJOLD_AID = "F222222222";
    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private static final String SELECT_APDU_HEADER = "00A40400";
    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
    private static final byte[] SELECT_APDU = BuildSelectApdu(OTURGJOLD_AID);

    /**
     * Called if the connection to the NFC card is lost, in order to let the application know the
     * cause for the disconnection (either a lost link, or another AID being selected by the
     * reader).
     *
     * @param reason Either DEACTIVATION_LINK_LOSS or DEACTIVATION_DESELECTED
     */
    @Override
    public void onDeactivated(int reason) {
    }

    /**
     * This method will be called when a command APDU has been received from a remote device. A
     * response APDU can be provided directly by returning a byte-array in this method. Should
     * return quickly since the user is likely in contact with reader at this point and easy to
     * lose connection.
     *
     * @param commandApdu The APDU that received from the remote device
     * @param extras      A bundle containing extra data. May be null.
     * @return a byte-array containing the response APDU, or null if no response APDU can be sent
     * at this point.
     */
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        // Only respond when the APDU matches our SELECT AID.
        if (Arrays.equals(SELECT_APDU, commandApdu)) {

            // Get the last input PIN
            String pin = getPin();

            if (pin.length() != 4) {
                // PIN is not ready, just be quiet - payment activity was launched from getPIN()
                return null;
            }

            String toSend = toSend();

            if (toSend == null) {
                Toast toast = Toast.makeText(this, R.string.info_no_token_available, Toast.LENGTH_LONG);
                toast.show();
                return UNKNOWN_CMD_SW;
            }

            //Letting the user know token was sent
            Toast.makeText(this, R.string.info_payment_sent, Toast.LENGTH_LONG).show();

            byte[] accountBytes = toSend.getBytes();

            return ConcatArrays(accountBytes, SELECT_OK_SW);
        } else {
            return UNKNOWN_CMD_SW;
        }
    }

    /**
     * Get the entered pin stored in preferences or trigger pin input activity if pin is not ready
     *
     * @return String the entered pin
     */
    private String getPin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String pin = sp.getString("lastPIN", "");

        // if pin expired or not ready then activate pin input
        if (pin.contentEquals("used") || pin.length() != 4) {
            // Trigger PIN input
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("MSG_REQUEST_PIN", "true");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            pin = "";
        } else {
            SharedPreferences.Editor clearPIN = PreferenceManager.getDefaultSharedPreferences(this).edit();
            // Signal to PIN activity that the pin has been used
            clearPIN.putString("lastPIN", "used");
            // Commit rather than apply, this should be done ASAP
            clearPIN.commit();
        }
        // Last entered PIN is present
        return pin;
    }

    // Template to build the JSON response sent to PayBuddy
    private static final String JSON_PATTERN = "{\"tokenitem\":\"%s\",\"device_id\":\"%s\"}";

    /**
     * Build the payment cryptogram to send to the POS machine
     * <p/>
     * Currently this only gets the next token from the selected card in accordance
     * with our simple payment flow mocking
     *
     * @return
     */
    private String toSend() {
        String tokenParam = null;
        String deviceParam = null;

        // Communicate with the service
        try {
            // get currently selected card
            Card card = Repository.getSelectedCard(getApplication());

            // Make JSON
            Token ct = Repository.getToken(getApplication(), card.getCard_id());
            int i = Repository.getTokenCount(getApplication(), card.getCard_id());

            if (ct != null) {
                tokenParam = ct.getTokenitem();
                deviceParam = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }

            // Get a new token if there is a network connection
            Context ctx = getApplication();
            if (NetworkUtil.isConnected(ctx)) {
                Gson gson = new Gson();
                String tokenJson = gson.toJson(ct, Token.class);
                new GetTokenTask(getApplication()).execute(getString(R.string.service_token_url), tokenJson);
            }
            // Setup network monitoring if not connected
            else {
                NetworkUtil.enableNetworkMonitoring(ctx);
            }

            if (ct == null) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return String.format(JSON_PATTERN, tokenParam, deviceParam);
    }


    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    private static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    private static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     * <p/>
     * Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    private static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     *
     * @param first First array
     * @param rest  Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    private static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
