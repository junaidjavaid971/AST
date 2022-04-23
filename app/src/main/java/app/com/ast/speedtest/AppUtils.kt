package app.com.ast.speedtest

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

class AppUtils {

    companion object {

        fun getNetworkType(context: Context): Int {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            if (info == null || !info.isConnected)
                return Constants.NOT_CONNECTED
            if (info.type == ConnectivityManager.TYPE_WIFI)
                return Constants.WIFI_CONST
            if (info.type == ConnectivityManager.TYPE_MOBILE) {
                val networkType = info.subtype
                when (networkType) {
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B   // api< 9: replace by 12
                        , TelephonyManager.NETWORK_TYPE_EHRPD    // api<11: replace by 14
                        , TelephonyManager.NETWORK_TYPE_HSPAP    // api<13: replace by 15
                        , TelephonyManager.NETWORK_TYPE_TD_SCDMA // api<25: replace by 17
                    -> return Constants.THREEG
                    TelephonyManager.NETWORK_TYPE_LTE      // api<11: replace by 13
                        , TelephonyManager.NETWORK_TYPE_IWLAN    // api<25: replace by 18
                        , 19
                    -> return Constants.FOURG
                    else -> return Constants.UNKKNOWN
                }
            }
            return Constants.UNKKNOWN
        }

    }

}