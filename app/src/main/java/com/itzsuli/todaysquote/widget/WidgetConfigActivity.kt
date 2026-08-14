package com.itzsuli.todaysquote.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.itzsuli.todaysquote.data.QuoteRepository
import com.itzsuli.todaysquote.ui.TodaysQuoteTheme
import com.itzsuli.todaysquote.ui.WidgetStudioScreen

/**
 * Shown when a widget is dropped on the home screen, and again whenever the user long-presses
 * it and picks the edit affordance (the provider declares `reconfigurable`).
 */
class WidgetConfigActivity : ComponentActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Back out = widget not added. Set this first so it holds if the user leaves.
        setResult(Activity.RESULT_CANCELED, resultIntent())

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val repo = QuoteRepository.get(this)
        setContent {
            TodaysQuoteTheme(darkTheme = true) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0E131D),
                                    Color(0xFF070910),
                                    Color(0xFF0A0D14)
                                )
                            )
                        )
                ) {
                    WidgetStudioScreen(
                        repo = repo,
                        widgetId = widgetId,
                        onDone = { settings ->
                            WidgetPrefs.save(this@WidgetConfigActivity, widgetId, settings)
                            WidgetRenderer.update(
                                this@WidgetConfigActivity,
                                AppWidgetManager.getInstance(this@WidgetConfigActivity),
                                widgetId
                            )
                            MidnightScheduler.schedule(this@WidgetConfigActivity)
                            setResult(Activity.RESULT_OK, resultIntent())
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun resultIntent() =
        Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
}
