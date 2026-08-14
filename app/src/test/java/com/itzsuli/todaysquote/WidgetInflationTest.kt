package com.itzsuli.todaysquote

import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.itzsuli.todaysquote.data.BuiltInQuotes
import com.itzsuli.todaysquote.widget.QuoteSource
import com.itzsuli.todaysquote.widget.TapAction
import com.itzsuli.todaysquote.widget.WidgetAlign
import com.itzsuli.todaysquote.widget.WidgetFont
import com.itzsuli.todaysquote.widget.WidgetRenderer
import com.itzsuli.todaysquote.widget.WidgetSettings
import com.itzsuli.todaysquote.widget.WidgetTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The launcher, not this app, inflates a widget's RemoteViews — and it refuses any view class
 * that isn't annotated @RemoteView, and any setter that isn't @RemotableViewMethod. Both
 * failures surface only as "couldn't add widget" on the home screen, so the only honest test
 * is to apply the RemoteViews for real and see whether it inflates.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WidgetInflationTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val quote = BuiltInQuotes.all.first()

    private fun inflate(settings: WidgetSettings, width: Int = 250, height: Int = 110) {
        val views = WidgetRenderer.buildViews(
            context = context,
            widgetId = 42,
            settings = settings,
            quote = quote,
            widthDp = width,
            heightDp = height
        )
        // apply() is what the launcher does; it throws if the layout or any action is illegal.
        views.apply(context, FrameLayout(context))
    }

    @Test
    fun `the default widget inflates`() {
        inflate(WidgetSettings())
    }

    @Test
    fun `every skin inflates`() {
        WidgetTheme.entries.forEach { theme ->
            inflate(WidgetSettings(theme = theme))
        }
    }

    @Test
    fun `every typeface inflates`() {
        WidgetFont.entries.forEach { font ->
            inflate(WidgetSettings(font = font))
        }
    }

    @Test
    fun `every alignment inflates`() {
        WidgetAlign.entries.forEach { align ->
            inflate(WidgetSettings(align = align))
        }
    }

    @Test
    fun `every tap action inflates`() {
        TapAction.entries.forEach { action ->
            inflate(WidgetSettings(tapAction = action))
        }
    }

    @Test
    fun `every source inflates`() {
        QuoteSource.entries.forEach { source ->
            inflate(WidgetSettings(source = source))
        }
    }

    @Test
    fun `it inflates with the author line and shuffle button hidden`() {
        inflate(WidgetSettings(showAuthor = false, showShuffle = false))
    }

    @Test
    fun `it inflates at the extremes of the resize range`() {
        inflate(WidgetSettings(), width = 110, height = 60)
        inflate(WidgetSettings(), width = 640, height = 480)
    }

    @Test
    fun `it inflates with no quote to show`() {
        val views = WidgetRenderer.buildViews(
            context = context,
            widgetId = 42,
            settings = WidgetSettings(),
            quote = null,
            widthDp = 250,
            heightDp = 110
        )
        views.apply(context, FrameLayout(context))
    }
}
