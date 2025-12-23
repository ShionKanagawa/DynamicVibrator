/*
 * Copyright (c) 2025 Alcatraz323 <alcatraz32323@gmail.com>
 * Copyright (C) 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package io.github.shionkanagawa.dynamicvibrator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.shionkanagawa.dynamicvibrator.ui.SettingsScreen

class MediaVibrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // SettingsScreen Composable is the entire UI of our app.
            SettingsScreen()
        }
    }
}