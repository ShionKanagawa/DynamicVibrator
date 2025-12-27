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

    private const val DEFAULT_ENABLE_STATE = false
    private const val DEFAULT_LEVEL = 1
    private const val DEFAULT_LATENCY = 0

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
            if (DEFAULT_ENABLE_STATE) 1 else 0
        ) == 1
    }

    fun getMediaVibrationLevel(context: Context): Int {
        return Settings.Secure.getInt(
            context.contentResolver,
            PREF_MEDIA_VIB_LEVEL,
            DEFAULT_LEVEL
        )
    }

    fun getMediaVibrationLatency(context: Context): Int {
        return Settings.Secure.getInt(
            context.contentResolver,
            PREF_MEDIA_VIB_LATENCY,
            DEFAULT_LATENCY
        )
    }
    
    fun onBoot(context: Context) {
        val shouldBeEnabled = getMediaVibrationState(context)
        Log.d(TAG, "onBoot called. Should be enabled: $shouldBeEnabled")

        if (shouldBeEnabled) {
            try {
                // This is a robust way to ensure settings are applied on boot.
                // We don't do the forceful off/on here to avoid race conditions on boot.
                // Instead, we just apply the full set of current parameters.
                val am = context.getSystemService(AudioManager::class.java)
                am.setParameters("somc.media_vibration=1")
                am.setParameters("somc.media_vibration_vol_idx=${getMediaVibrationLevel(context)}")
                am.setParameters("somc.media_vibration_bt_delay=${500 - getMediaVibrationLatency(context)}")
                
                val serviceIntent = Intent(context, VolumeListenerService::class.java)
                context.startService(serviceIntent)
                Log.d(TAG, "Boot settings applied successfully.")

            } catch (e: Exception) {
                Log.e(TAG, "Failed during onBoot initialization", e)
            }
        }
    }
}