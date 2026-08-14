package com.itzsuli.todaysquote.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle

class QuoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { WidgetRenderer.update(context, appWidgetManager, it) }
        MidnightScheduler.schedule(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        // The user resized the widget: re-render so the type fits the new cell.
        WidgetRenderer.update(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { WidgetPrefs.delete(context, it) }
    }

    override fun onEnabled(context: Context) = MidnightScheduler.schedule(context)

    override fun onDisabled(context: Context) = MidnightScheduler.cancel(context)

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_SHUFFLE -> {
                val id = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val current = WidgetPrefs.load(context, id)
                    WidgetPrefs.save(
                        context,
                        id,
                        current.copy(shuffleOffset = current.shuffleOffset + 1)
                    )
                    WidgetRenderer.update(context, AppWidgetManager.getInstance(context), id)
                }
            }

            ACTION_REFRESH_ALL -> WidgetRenderer.updateAll(context)
        }
    }

    companion object {
        const val ACTION_SHUFFLE = "com.itzsuli.todaysquote.SHUFFLE"
        const val ACTION_REFRESH_ALL = "com.itzsuli.todaysquote.REFRESH_ALL"

        /** Called from the app whenever quotes or settings change. */
        fun refreshAll(context: Context) {
            context.sendBroadcast(
                Intent(context, QuoteWidgetProvider::class.java).setAction(ACTION_REFRESH_ALL)
            )
        }
    }
}

/**
 * Rolls widgets over at midnight. Belt and braces, because a widget showing yesterday's
 * quote is the one bug that would be noticed every single day:
 *  1. an inexact alarm just after midnight (no exact-alarm permission needed),
 *  2. the system's ACTION_DATE_CHANGED broadcast,
 *  3. the provider's own updatePeriodMillis as a slow backstop.
 * All three converge on the same derived quote, so firing more than once is harmless.
 */
object MidnightScheduler {

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val triggerAt = System.currentTimeMillis() +
            com.itzsuli.todaysquote.data.DailyPicker.millisUntilNextMidnight()
        alarmManager.set(AlarmManager.RTC, triggerAt, pendingIntent(context))
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java)?.cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DayChangeReceiver::class.java)
            .setAction(DayChangeReceiver.ACTION_MIDNIGHT)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
