package ru.tradernet.utils

import android.content.Context
import android.net.ConnectivityManager
import java.lang.IllegalArgumentException

fun isNetworkConnected(context: Context?): Boolean {
    if(context == null) {
        throw IllegalArgumentException("set android context")
    }
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork!= null && activeNetwork.isConnected
}
