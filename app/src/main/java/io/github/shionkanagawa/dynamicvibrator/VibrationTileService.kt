package io.github.shionkanagawa.dynamicvibrator

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class VibrationTileService : TileService() {

    // Called when the user adds the tile to their Quick Settings.
    override fun onTileAdded() {
        super.onTileAdded()
        updateTile()
    }

    // Called when the tile becomes visible.
    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    // Called when the user clicks the tile.
    override fun onClick() {
        super.onClick()
        cycleState()
        updateTile()
    }

    private fun cycleState() {
        val isEnabled = MediaVibration.getMediaVibrationState(this)
        var currentLevel = MediaVibration.getMediaVibrationLevel(this)

        if (!isEnabled) {
            // State: Off -> Low
            MediaVibration.switchMediaVibration(this, true)
            MediaVibration.setMediaVibrationLevel(this, 1) // Set to Low
        } else {
            // Cycle through Low -> Medium -> High -> Off
            currentLevel++
            if (currentLevel > 3) {
                // State: High -> Off
                MediaVibration.switchMediaVibration(this, false)
            } else {
                // State: Low -> Medium or Medium -> High
                MediaVibration.setMediaVibrationLevel(this, currentLevel)
            }
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return // The tile is not available yet.
        val isEnabled = MediaVibration.getMediaVibrationState(this)
        val level = MediaVibration.getMediaVibrationLevel(this)

        if (!isEnabled) {
            // State: Off
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.qs_tile_label)
            tile.subtitle = getString(R.string.setting_level_off)
        } else {
            // State: Low, Medium, or High
            tile.state = Tile.STATE_ACTIVE
            tile.label = getString(R.string.qs_tile_label)
            tile.subtitle = when (level) {
                1 -> getString(R.string.setting_level_low)
                2 -> getString(R.string.setting_level_medium)
                3 -> getString(R.string.setting_level_high)
                else -> getString(R.string.setting_level_low) // Fallback
            }
        }
        tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_vibration)
        tile.updateTile()
    }
}