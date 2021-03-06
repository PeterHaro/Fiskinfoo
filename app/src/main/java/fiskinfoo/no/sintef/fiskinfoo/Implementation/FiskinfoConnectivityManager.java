package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Check device's network connectivity and speed
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
public class FiskinfoConnectivityManager {

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = FiskinfoConnectivityManager.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = FiskinfoConnectivityManager.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedValidWifi(Context context) {
        if (FiskinfoConnectivityManager.isConnectedWifi(context)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            SupplicantState wifiState = wifiInfo.getSupplicantState();
            if (wifiState == SupplicantState.COMPLETED) {
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("http://www.google.com");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (!url.getHost().equals(urlConnection.getURL().getHost())) {
                        return false;
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isConnectedValidWifi2(Context context) {
        if (FiskinfoConnectivityManager.isConnectedWifi(context)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            SupplicantState wifiState = wifiInfo.getSupplicantState();
            if (wifiState == SupplicantState.COMPLETED) {
                try {
                    //parse url. if url is not parsed properly then return
                    URL url;
                    try {
                        url = new URL("https://clients3.google.com/generate_204");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return false;
                    }

                    //open connection. If fails return false
//                    HttpURLConnection urlConnection;
                    HttpsURLConnection urlConnection;
                    try {
                        urlConnection = (HttpsURLConnection) url.openConnection();

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                    urlConnection.setRequestProperty("User-Agent", "Android");
                    urlConnection.setRequestProperty("Connection", "close");
                    urlConnection.setConnectTimeout(1500);
                    urlConnection.connect();
                    return urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return false;
    }



    public static boolean hasValidNetworkConnection(Context context) {
        return isConnectedMobile(context) || isConnectedValidWifi2(context);
    }



    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = FiskinfoConnectivityManager.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = FiskinfoConnectivityManager.getNetworkInfo(context);
        return (info != null && info.isConnected() && FiskinfoConnectivityManager.isConnectionFast(info.getType(), info.getSubtype()));
    }

    public static String calculateWifiSpeed(Context context) {
        WifiManager wifiObj = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiObj.getConnectionInfo();
        String units = "";
        if (wifiInfo != null) {
            Integer linkSpeed = wifiInfo.getLinkSpeed();
            units = WifiInfo.LINK_SPEED_UNITS;
        }
        return units;
    }

    /*
     * Check if the connection is fast
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

}