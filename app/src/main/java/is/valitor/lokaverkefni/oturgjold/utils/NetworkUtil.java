package is.valitor.lokaverkefni.oturgjold.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * For controlling examination of network status.
 * Created by eggert on 10/04/15.
 */
public class NetworkUtil {

    /**
     * Check if the device has network connectivity
     *
     * @param ctx - provided context
     * @return boolean, true if device has network connection
     */
    public static boolean isConnected(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo.isConnected();
    }

    /**
     * Enable the listener that monitor changes in connectivity
     *
     * @param ctx - provided context
     */
    public static void enableNetworkMonitoring(Context ctx) {
        ComponentName receiver = new ComponentName(ctx, NetworkChangeReceiver.class);

        PackageManager pm = ctx.getPackageManager();

        // use DONT_KILL_APP flag to keep it alive if at all possible
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
//                PackageManager.DONT_KILL_APP);
    }

    /**
     * Disable the listener that monitor changes in connectivity
     *
     * @param ctx - provided context
     */
    public static void disableNetworkMonitoring(Context ctx) {
        ComponentName receiver = new ComponentName(ctx, NetworkChangeReceiver.class);

        PackageManager pm = ctx.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                0);
    }

}
