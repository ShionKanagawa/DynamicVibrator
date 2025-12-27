/*
 * Copyright (c) 2025 Alcatraz323 <alcatraz32323@gmail.com>
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package io.github.shionkanagawa.dynamicvibrator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.shionkanagawa.dynamicvibrator.ui.SettingsScreen

class MediaVibrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        // This is the correct, non-destructive failsafe.
        // It ensures the service is running if the setting is ON,
        // without wrongfully changing the setting itself.
        if (MediaVibration.getMediaVibrationState(this)) {
            val serviceIntent = Intent(this, VolumeListenerService::class.java)
            startService(serviceIntent)
        }
    }
}