package com.itzsuli.todaysquote.notify

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.itzsuli.todaysquote.MainActivity
import com.itzsuli.todaysquote.R
import com.itzsuli.todaysquote.widget.WidgetPrefs
import com.itzsuli.todaysquote.widget.WidgetRenderer
import java.util.Calendar

/** Optional once-a-day nudge carrying the day's quote. Off until the user turns it on. */
object DailyNotifier {

    private const val PREFS = "notify"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_HOUR = "hour"
    private const val KEY_MINUTE = "minute"
    private const val CHANNEL_ID = "daily_quote"
    private const val NOTIFICATION_ID = 1001

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(context: Context) = prefs(context).getBoolean(KEY_ENABLED, false)
    fun hour(context: Context) = prefs(context).getInt(KEY_HOUR, 8)
    fun minute(context: Context) = prefs(context).getInt(KEY_MINUTE, 0)

    fun configure(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        prefs(context).edit()
            .putBoolean(KEY_ENABLED, enabled)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
        reschedule(context)
    }

    fun reschedule(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pending = pendingIntent(context)
        alarmManager.cancel(pending)
        if (!isEnabled(context)) return

        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour(context))
            set(Calendar.MINUTE, minute(context))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, next.timeInMillis, pending)
    }

    fun notifyNow(context: Context) {
        if (!isEnabled(context)) return
        if (!hasPermission(context)) return

        val settings = WidgetPrefs.loadDefaults(context)
        val quote = WidgetRenderer.pickQuote(context, settings, WidgetPrefs.DEFAULTS_ID) ?: return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = context.getString(R.string.notification_channel_desc) }
        )

        val open = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(quote.displayAuthor)
            .setContentText(quote.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote.text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    fun hasPermission(context: Context): Boolean =
        android.os.Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyNotificationReceiver::class.java)
            .setAction(DailyNotificationReceiver.ACTION_NOTIFY)
        return PendingIntent.getBroadcast(
            context, 42, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DailyNotifier.notifyNow(context)
        DailyNotifier.reschedule(context)
    }

    companion object {
        const val ACTION_NOTIFY = "com.itzsuli.todaysquote.NOTIFY"
    }
}
