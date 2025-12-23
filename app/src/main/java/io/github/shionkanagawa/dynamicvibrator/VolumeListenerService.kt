/*
 * Copyright (c) 2025 Alcatraz323 <alcatraz32323@gmail.com>
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package io.github.shionkanagawa.dynamicvibrator

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder

class VolumeListenerService : Service() {
    private var volumeListenerReceiver: VolumeListenerReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (volumeListenerReceiver == null) {
            volumeListenerReceiver = VolumeListenerReceiver()
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION")
            registerReceiver(volumeListenerReceiver, intentFilter)
        }
        
        // Initial sync
        val audioManager = getSystemService(AudioManager::class.java)
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (current < VolumeListenerReceiver.kMediaVibVolTable.size) {
            val volumeValue = VolumeListenerReceiver.kMediaVibVolTable[current]
            audioManager.setParameters("somc.media_vibration_audio_volume=$volumeValue")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (volumeListenerReceiver != null) {
            unregisterReceiver(volumeListenerReceiver)
            volumeListenerReceiver = null
        }
    }

    internal class VolumeListenerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (context == null) return

            if (intent.action == "android.media.VOLUME_CHANGED_ACTION" &&
                intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) == AudioManager.STREAM_MUSIC) {
                
                val audioManager = context.getSystemService(AudioManager::class.java)
                val current = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1)
                
                if (current != -1 && current < kMediaVibVolTable.size) {
                    val volumeValue = kMediaVibVolTable[current]
                    audioManager.setParameters("somc.media_vibration_audio_volume=$volumeValue")
                }
            }
        }

        companion object {
            val kMediaVibVolTable: FloatArray = floatArrayOf(
                0.0000000000f, 0.0068881093f, 0.0078567471f, 0.0093633886f,
                0.0106801121f, 0.0121819992f, 0.0145180682f, 0.0165596586f,
                0.0188883636f, 0.0225104671f, 0.0256759953f, 0.0292866733f,
                0.0349028111f, 0.0398109965f, 0.0501190498f, 0.0681296214f,
                0.0857700631f, 0.1079780608f, 0.1467805654f, 0.1847856790f,
                0.2326312810f, 0.3162285388f, 0.3981079757f, 0.4410067499f,
                0.5054804087f, 0.5599492788f, 0.6202875376f, 0.7109714746f,
                0.7875833511f, 0.8724506497f, 1.0000000000f
            )
        }
    }
}