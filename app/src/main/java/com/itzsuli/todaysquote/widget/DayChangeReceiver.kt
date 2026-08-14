package com.itzsuli.todaysquote.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.itzsuli.todaysquote.notify.DailyNotifier

/**
 * Wakes on the day rolling over (or the clock/timezone moving, or a reboot) and repaints
 * every widget, then arms the next alarm.
 */
class DayChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        WidgetRenderer.updateAll(context)
        MidnightScheduler.schedule(context)
        DailyNotifier.reschedule(context)
    }

    companion object {
        const val ACTION_MIDNIGHT = "com.itzsuli.todaysquote.MIDNIGHT"
    }
}
