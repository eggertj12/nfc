package is.valitor.lokaverkefni.oturgjold.utils;

/**
 * Created by dasve_000 on 5/5/2015.
 */
public class FourDigitUngrouper {

    public static String RemoveSpaces(String s) {
        String ret = "";
        String[] shit = s.split(" ");
        for(String r : shit) {
            ret = ret.concat(r);
        }
        return ret;
    }
}
