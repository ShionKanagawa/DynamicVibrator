package io.github.shionkanagawa.dynamicvibrator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
      Log.d("BootCompletedReceiver", "Boot completed, applying vibration settings.")
      // Call the existing onBoot logic from MediaVibration
      MediaVibration.onBoot(context)
    }
  }
}
