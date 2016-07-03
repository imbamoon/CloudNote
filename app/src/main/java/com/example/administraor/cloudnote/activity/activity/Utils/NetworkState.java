package com.example.administraor.cloudnote.activity.activity.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zyw on 2016/6/3.
 */
public class NetworkState {
//    //判断是否有网
//    public boolean hasNetwork(Context context) {
//        return isNetworkConnected(context) || isWifiConnected(context) || isMobileConnected(context);
//    }

    //判断是否有网络连接
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

//    //判断WIFI网络是否可用
//    public boolean isWifiConnected(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
//                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            if (mWiFiNetworkInfo != null) {
//                return mWiFiNetworkInfo.isAvailable();
//            }
//        }
//        return false;
//    }
//
//    //判断MOBILE网络是否可用
//    public boolean isMobileConnected(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mMobileNetworkInfo = mConnectivityManager
//                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//            if (mMobileNetworkInfo != null) {
//                return mMobileNetworkInfo.isAvailable();
//            }
//        }
//        return false;
//    }
}
