package recipeking.uw.tacoma.edu.recipeking.utils;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class for network related functions.
 */
public class NetworkUtils {

    /**
     * Checks to see if there is a network connection available.
     * @return true if there is available connection, false otherwise.
     */
    public static boolean networkConnectionAvailable(Activity currentActivity) {
        boolean result = false;

        ConnectivityManager connMgr = (ConnectivityManager)
                currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            result = true;
        }

        return result;
    }

}
