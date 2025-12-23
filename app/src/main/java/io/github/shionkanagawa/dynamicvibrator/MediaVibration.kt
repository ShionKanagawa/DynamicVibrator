/*
 * Copyright (c) 2025 Alcatraz323 <alcatraz32323@gmail.com>
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package io.github.shionkanagawa.dynamicvibrator

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import android.util.Log

object MediaVibration {

    private const val TAG = "MediaVibration"
    private const val PREF_MEDIA_VIB_ENABLE: String = "mediavib_enable"
    private const val PREF_MEDIA_VIB_LEVEL: String = "mediavib_level"
    private const val PREF_MEDIA_VIB_LATENCY: String = "mediavib_latency"

    fun switchMediaVibration(context: Context, enable: Boolean) {
        try {
            val am = context.getSystemService(AudioManager::class.java)
            val paramStr = if (enable) "1" else "0"
            am!!.setParameters("somc.media_vibration=$paramStr")
            Settings.Secure.putInt(
                context.contentResolver,
                PREF_MEDIA_VIB_ENABLE,
                if (enable) 1 else 0
            )

            // Automatically start or stop the listener service
            val serviceIntent = Intent(context, VolumeListenerService::class.java)
            if (enable) {
                context.startService(serviceIntent)
            } else {
                context.stopService(serviceIntent)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to switch media vibration", e)
        }
    }

    fun isMediaVibAvailable(context: Context): Boolean {
        return try {
            val am = context.getSystemService(AudioManager::class.java)
            am!!.getParameters("somc.media_vibration") != ""
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check media vibration availability", e)
            false
        }
    }

    fun setMediaVibrationLevel(context: Context, level: Int) {
        try {
            val am = context.getSystemService(AudioManager::class.java)
            am!!.setParameters("somc.media_vibration_vol_idx=$level")
            Settings.Secure.putInt(
                context.contentResolver,
                PREF_MEDIA_VIB_LEVEL,
                level
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set media vibration level", e)
        }
    }

    fun setMediaVibrationLatency(context: Context, latency: Int) {
        try {
            val am = context.getSystemService(AudioManager::class.java)
            am!!.setParameters("somc.media_vibration_bt_delay=${500 - latency}")
            Settings.Secure.putInt(
                context.contentResolver,
                PREF_MEDIA_VIB_LATENCY,
                latency
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set media vibration latency", e)
        }
    }

    fun getMediaVibrationState(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            PREF_MEDIA_VIB_ENABLE,
            0 // Default to off
        ) == 1
    }

    fun getMediaVibrationLevel(context: Context): Int {
        return Settings.Secure.getInt(
            context.contentResolver,
            PREF_MEDIA_VIB_LEVEL,
            1 // Default to 1 (Low)
        )
    }

    fun getMediaVibrationLatency(context: Context): Int {
        return Settings.Secure.getInt(
            context.contentResolver,
            PREF_MEDIA_VIB_LATENCY,
            0 // Default to 0ms
        )
    }

    fun onBoot(context: Context) {
        if (getMediaVibrationState(context)) {
             switchMediaVibration(context, true)
             setMediaVibrationLevel(context, getMediaVibrationLevel(context))
             setMediaVibrationLatency(context, getMediaVibrationLatency(context))
        }
    }
}