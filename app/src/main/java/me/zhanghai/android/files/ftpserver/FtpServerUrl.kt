/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.ftpserver

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.content.Context
import me.zhanghai.android.files.settings.Settings
import me.zhanghai.android.files.util.RuntimeNetworkCallback
import me.zhanghai.android.files.util.getLocalAddress
import me.zhanghai.android.files.util.valueCompat
import java.net.InetAddress

object FtpServerUrl {
    fun getUrl(): String? {
        val localAddress = InetAddress::class.getLocalAddress() ?: return null
        val username = if (!Settings.FTP_SERVER_ANONYMOUS_LOGIN.valueCompat) {
            Settings.FTP_SERVER_USERNAME.valueCompat
        } else {
            null
        }
        val host = localAddress.hostAddress
        val port = Settings.FTP_SERVER_PORT.valueCompat
        return "ftp://${if (username != null) "$username@" else ""}$host:$port/"
    }

    fun createChangeReceiver(context: Context, onChange: () -> Unit): RuntimeNetworkCallback {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
            
        return RuntimeNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    onChange()
                }
                
                override fun onLost(network: Network) {
                    onChange()
                }
                
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    onChange()
                }
            },
            context
        )
    }
}
