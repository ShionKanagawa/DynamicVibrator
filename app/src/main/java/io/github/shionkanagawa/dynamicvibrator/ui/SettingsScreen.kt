package io.github.shionkanagawa.dynamicvibrator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.shionkanagawa.dynamicvibrator.MediaVibration
import io.github.shionkanagawa.dynamicvibrator.R
import io.github.shionkanagawa.dynamicvibrator.ui.theme.DynamicVibratorTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val isEnabled = remember { mutableStateOf(MediaVibration.getMediaVibrationState(context)) }
    val level = remember { mutableIntStateOf(MediaVibration.getMediaVibrationLevel(context)) }
    val latency = remember { mutableIntStateOf(MediaVibration.getMediaVibrationLatency(context)) }

    DynamicVibratorTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = { Text(stringResource(id = R.string.settings_title)) },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                item {
                    SettingsGroup(title = stringResource(id = R.string.group_title_general)) {
                        SwitchSetting(
                            title = stringResource(id = R.string.setting_enable_title),
                            summary = stringResource(id = R.string.setting_enable_summary),
                            checked = isEnabled.value,
                            haptics = haptics,
                            onCheckedChange = {
                                isEnabled.value = it
                                MediaVibration.switchMediaVibration(context, it)
                            }
                        )
                    }
                }
                item {
                    SettingsGroup(title = stringResource(id = R.string.group_title_controls)) {
                        SteppedSliderSetting(
                            title = stringResource(id = R.string.setting_level_title),
                            value = level.intValue,
                            enabled = isEnabled.value,
                            labels = listOf(
                                stringResource(id = R.string.setting_level_low),
                                stringResource(id = R.string.setting_level_medium),
                                stringResource(id = R.string.setting_level_high)
                            ),
                            haptics = haptics,
                            onValueChange = {
                                val apiValue = it + 1
                                level.intValue = apiValue
                                MediaVibration.setMediaVibrationLevel(context, apiValue)
                            }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        SliderSetting(
                            title = stringResource(id = R.string.setting_latency_title),
                            value = latency.intValue,
                            enabled = isEnabled.value,
                            haptics = haptics,
                            onValueChange = {
                                latency.intValue = it
                                MediaVibration.setMediaVibrationLatency(context, it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        Column(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceContainer)) {
            content()
        }
    }
}

@Composable
fun SwitchSetting(title: String, summary: String, checked: Boolean, haptics: HapticFeedback, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onCheckedChange(it)
        })
    }
}

@Composable
fun SteppedSliderSetting(title: String, value: Int, enabled: Boolean, labels: List<String>, haptics: HapticFeedback, onValueChange: (Int) -> Unit) {
    val sliderIndex = (value - 1).coerceIn(0, labels.size - 1)
    val valueRange = 0f..(labels.size - 1).toFloat()
    val steps = labels.size - 2

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        val titleColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        val label = labels.getOrElse(sliderIndex) { labels.first() }
        Text(text = "$title: $label", style = MaterialTheme.typography.bodyLarge, color = titleColor)
        Spacer(Modifier.height(4.dp))
        Slider(
            value = sliderIndex.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            onValueChangeFinished = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
            valueRange = valueRange,
            steps = steps,
            enabled = enabled
        )
    }
}

@Composable
fun SliderSetting(title: String, value: Int, enabled: Boolean, haptics: HapticFeedback, onValueChange: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        val titleColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        Text(text = "$title: " + stringResource(id = R.string.setting_latency_unit_ms, value), style = MaterialTheme.typography.bodyLarge, color = titleColor)
        Spacer(Modifier.height(4.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            onValueChangeFinished = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
            valueRange = 0f..500f,
            enabled = enabled
        )
    }
}