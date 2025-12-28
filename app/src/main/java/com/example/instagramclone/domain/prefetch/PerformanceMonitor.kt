package com.example.instagramclone.domain.prefetch

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class NetworkType {
    WIFI, CELLULAR_4G, CELLULAR_3G, NONE
}

enum class PowerState {
    NORMAL, LOW_BATTERY, POWER_SAVER
}

class PerformanceMonitor @Inject constructor(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun getNetworkType(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                // Simplified logic: checking for 4G vs 3G would usually involve TelephonyManager
                // For this sample, we'll assume cellular is 4G unless signal is very low (not easily detectable here)
                // In a real app, you'd check LinkDownstreamBandwidthKbps
                val bandwidth = capabilities.linkDownstreamBandwidthKbps
                if (bandwidth > 2000) NetworkType.CELLULAR_4G else NetworkType.CELLULAR_3G
            }

            else -> NetworkType.NONE
        }
    }

    fun getPowerState(): PowerState {
        if (powerManager.isPowerSaveMode) return PowerState.POWER_SAVER

        val batteryStatus = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()

        return if (batteryPct < 15) PowerState.LOW_BATTERY else PowerState.NORMAL
    }

    fun isPrefetchEnabled(): Boolean {
        val power = getPowerState()
        val network = getNetworkType()
        return power == PowerState.NORMAL && network != NetworkType.NONE
    }
}
