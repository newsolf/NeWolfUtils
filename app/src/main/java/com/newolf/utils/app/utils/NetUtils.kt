package com.newolf.utils.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import java.io.InputStreamReader
import java.io.LineNumberReader


/**
 * ================================================
 * @author : NeWolf
 * @version : 1.0
 * date :
 * desc:
 * history:
 * ================================================
 */
object NetUtils {
    val NETYPE_NOCON = -1
    val NETYPE_UNKNOWN = 0
    val NETYPE_WIFI = 1
    val NETYPE_2G = 2
    val NETYPE_3G = 3
    val NETYPE_4G = 4
    val NETYPE_TELECOM_2G = 5
    val NETYPE_MOBILE_UNICOM_2G = 6
    val NETYPE_TELECOM_3G = 7
    val NETYPE_MOBILE_3G = 8
    val NETYPE_UNICOM_3G = 9
    val NETYPE_4G_UNKNOWN = 10
    fun getActiveNetworkInfo(context: Context?): NetworkInfo? {
        if (context == null) {
            return null
        } else {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeInfo: NetworkInfo? = null

            try {
                activeInfo = manager.activeNetworkInfo
            } catch (var4: Exception) {
            }

            return activeInfo
        }
    }

    fun getCurrentNetMode(context: Context): String {
        var netype: Int = 0
        var isConnected = ""
        val info = getActiveNetworkInfo(context)
        if (null != info) {
            isConnected = if(info.isConnected){"连接"}else{"未连接"}
            if (info.getType() == 1) {
                netype = 1
            } else {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val type = tm.networkType
                when (type) {
                    1, 2 -> netype = 6
                    3, 9, 10, 15 -> netype = 9
                    4 -> netype = 5
                    5, 6, 12 -> netype = 7
                    7, 11 -> netype = 2
                    8 -> netype = 8
                    13 -> netype = 4
                    14 -> netype = 3
                    else -> netype = 0
                }
            }
        } else {
            netype = -1
        }

        var netTypeString: String

        netTypeString = when (netype) {
            NETYPE_WIFI -> "WIFI"
            NETYPE_2G, NETYPE_TELECOM_2G, NETYPE_MOBILE_UNICOM_2G -> "2G"
            NETYPE_3G, NETYPE_TELECOM_3G, NETYPE_MOBILE_3G, NETYPE_UNICOM_3G -> "3G"
            NETYPE_4G, NETYPE_4G_UNKNOWN -> "4G"
            else -> "UNKNOWN"
        }


        return String.format("%s -- %s",netTypeString,isConnected)
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        } else {
            var connectivity: ConnectivityManager? = null

            try {
                connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connectivity == null) {
                    return false
                }

                val activeInfo = connectivity.activeNetworkInfo
                if (activeInfo != null) {
                    return activeInfo.isConnected
                }
            } catch (var3: Exception) {
            }

            return false
        }
    }

    fun isWifiConnected(context: Context?): Boolean {
        if (context == null) {
            return false
        } else {
            var isWifiConnected = false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                try {
                    val activeNetInfo = connectivityManager.activeNetworkInfo
                    if (activeNetInfo != null) {
                        isWifiConnected = 1 == activeNetInfo.type && activeNetInfo.isConnected
                    }
                } catch (var4: Exception) {
                }

            }

            return isWifiConnected
        }
    }

    fun pingBaidu(): String {
//        Thread{
        var returnMsg = ""
        try {
            val process = Runtime.getRuntime().exec("ping 8.8.8.8")

            val r = InputStreamReader(process.inputStream)

            val returnData: LineNumberReader = LineNumberReader(r)
            returnMsg = returnData.readLine()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return returnMsg;
//        }.start()

    }

}