/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.ConnectivityManager.NetworkCallback

class RuntimeNetworkCallback(
    private val networkRequest: NetworkRequest,
    private val networkCallback: NetworkCallback,
    private val context: Context
) {
    fun register() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun unregister() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
