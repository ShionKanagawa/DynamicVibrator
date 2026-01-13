package io.github.shionkanagawa.dynamicvibrator

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

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
        val useSubtitle = MediaVibration.getUseSubtitle(this)

        val levelName = when (level) {
            1 -> getString(R.string.setting_level_low)
            2 -> getString(R.string.setting_level_medium)
            3 -> getString(R.string.setting_level_high)
            else -> getString(R.string.setting_level_low) // Fallback
        }

        if (!isEnabled) {
            // State: Off
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.qs_tile_label)
            tile.subtitle = if (useSubtitle) getString(R.string.setting_level_off) else null
        } else {
            // State: Low, Medium, or High
            tile.state = Tile.STATE_ACTIVE
            if (useSubtitle) {
                tile.label = getString(R.string.qs_tile_label)
                tile.subtitle = levelName
            } else {
                tile.label = "${getString(R.string.qs_tile_label)}: $levelName"
                tile.subtitle = null
            }
        }
        tile.icon = Icon.createWithResource(this, R.drawable.ic_tile_vibration)
        tile.updateTile()
    }
}