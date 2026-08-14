package com.itzsuli.todaysquote.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import com.itzsuli.todaysquote.MainActivity
import com.itzsuli.todaysquote.R
import com.itzsuli.todaysquote.data.DailyPicker
import com.itzsuli.todaysquote.data.Quote
import com.itzsuli.todaysquote.data.QuoteRepository

object WidgetRenderer {

    /** Ids of every widget currently on a home screen. */
    fun placedWidgetIds(context: Context): IntArray =
        AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, QuoteWidgetProvider::class.java))

    /**
     * Push one style onto every placed widget, keeping each widget's own shuffle position so
     * restyling doesn't yank the quote out from under it.
     */
    fun applyToAll(context: Context, settings: WidgetSettings) {
        placedWidgetIds(context).forEach { id ->
            val existing = WidgetPrefs.load(context, id)
            WidgetPrefs.save(context, id, settings.copy(shuffleOffset = existing.shuffleOffset))
        }
        updateAll(context)
    }

    /** Redraw every placed widget. */
    fun updateAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, QuoteWidgetProvider::class.java))
        ids.forEach { update(context, manager, it) }
    }

    fun update(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val settings = WidgetPrefs.load(context, widgetId)
        val quote = pickQuote(context, settings, widgetId)
        val options = runCatching { manager.getAppWidgetOptions(widgetId) }.getOrNull()
        manager.updateAppWidget(widgetId, build(context, widgetId, settings, quote, options))
    }

    /** The pool this widget draws from, after source and category filters. */
    fun pool(context: Context, settings: WidgetSettings): List<Quote> {
        val repo = QuoteRepository.get(context)
        val favourites = repo.favourites.value
        val bySource = when (settings.source) {
            QuoteSource.ALL -> repo.allVisible
            QuoteSource.BUILT_IN -> repo.allVisible.filterNot { it.isCustom }
            QuoteSource.CUSTOM -> repo.custom.value
            QuoteSource.FAVOURITES -> repo.allVisible.filter { it.id in favourites }
        }
        val filtered =
            if (settings.categories.isEmpty()) bySource
            else bySource.filter { it.category in settings.categories }
        // Never leave a widget empty because of an over-tight filter.
        return filtered.ifEmpty { bySource.ifEmpty { repo.allVisible } }
    }

    fun pickQuote(context: Context, settings: WidgetSettings, widgetId: Int): Quote? =
        DailyPicker.pick(
            pool = pool(context, settings),
            day = DailyPicker.today(),
            salt = if (widgetId == WidgetPrefs.DEFAULTS_ID) 0 else widgetId,
            shuffleOffset = settings.shuffleOffset
        )

    private fun build(
        context: Context,
        widgetId: Int,
        settings: WidgetSettings,
        quote: Quote?,
        options: Bundle?
    ): RemoteViews = buildViews(
        context = context,
        widgetId = widgetId,
        settings = settings,
        quote = quote,
        widthDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
            ?.takeIf { it > 0 } ?: 250,
        heightDp = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
            ?.takeIf { it > 0 } ?: 110
    )

    /**
     * Builds the widget's RemoteViews. Separated from the AppWidgetManager plumbing so tests
     * can actually inflate the result — a RemoteViews only fails when the *launcher* applies
     * it, so building one proves nothing on its own.
     */
    fun buildViews(
        context: Context,
        widgetId: Int,
        settings: WidgetSettings,
        quote: Quote?,
        widthDp: Int,
        heightDp: Int
    ): RemoteViews {
        val layout = when (settings.font) {
            WidgetFont.SANS -> R.layout.widget_quote_sans
            WidgetFont.SERIF -> R.layout.widget_quote_serif
            WidgetFont.MONO -> R.layout.widget_quote_mono
        }
        val views = RemoteViews(context.packageName, layout)
        val theme = settings.theme

        // ------------------------------------------------------------------ background
        if (theme.bare) {
            views.setViewVisibility(R.id.widget_bg, View.GONE)
        } else {
            views.setViewVisibility(R.id.widget_bg, View.VISIBLE)
            views.setImageViewResource(R.id.widget_bg, theme.background)
            views.setInt(
                R.id.widget_bg,
                "setImageAlpha",
                (settings.opacity.coerceIn(0, 100) * 255 / 100)
            )
        }

        // ------------------------------------------------------------------------ text
        val text = quote?.text ?: context.getString(R.string.widget_empty)
        val textSizeSp = quoteTextSize(text.length, widthDp, heightDp) * settings.textScale
        views.setTextViewText(R.id.widget_quote, text)
        views.setTextColor(R.id.widget_quote, theme.textColor)
        views.setTextViewTextSize(R.id.widget_quote, TypedValue.COMPLEX_UNIT_SP, textSizeSp)

        val gravity = if (settings.align == WidgetAlign.CENTER) {
            android.view.Gravity.CENTER
        } else {
            android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
        }
        views.setInt(R.id.widget_content, "setGravity", gravity)
        views.setInt(R.id.widget_quote, "setGravity", gravity)

        // ---------------------------------------------------------------- author line
        val showAuthor = settings.showAuthor && quote != null && heightDp >= 70
        views.setViewVisibility(R.id.widget_author_row, if (showAuthor) View.VISIBLE else View.GONE)
        if (showAuthor) {
            views.setTextViewText(R.id.widget_author, quote.displayAuthor.uppercase())
            views.setTextColor(R.id.widget_author, theme.authorColor)
            views.setTextViewTextSize(
                R.id.widget_author,
                TypedValue.COMPLEX_UNIT_SP,
                (textSizeSp * 0.52f).coerceIn(9f, 13f)
            )
            views.setInt(R.id.widget_accent, "setBackgroundColor", theme.accentColor)
        }

        // -------------------------------------------------------------- shuffle button
        val showShuffle = settings.showShuffle && widgetId != WidgetPrefs.DEFAULTS_ID
        views.setViewVisibility(R.id.widget_shuffle, if (showShuffle) View.VISIBLE else View.GONE)
        if (showShuffle) {
            views.setInt(R.id.widget_shuffle, "setColorFilter", theme.accentColor)
            views.setOnClickPendingIntent(
                R.id.widget_shuffle,
                shufflePendingIntent(context, widgetId)
            )
        }

        // ------------------------------------------------------------------ body click
        val bodyIntent = when (settings.tapAction) {
            TapAction.OPEN_APP -> openAppPendingIntent(context, widgetId)
            TapAction.SHUFFLE -> shufflePendingIntent(context, widgetId)
            TapAction.NOTHING -> null
        }
        if (bodyIntent != null) views.setOnClickPendingIntent(R.id.widget_root, bodyIntent)

        views.setContentDescription(
            R.id.widget_root,
            quote?.let { "${it.text} — ${it.displayAuthor}" } ?: text
        )
        return views
    }

    fun quoteTextSize(length: Int, widthDp: Int, heightDp: Int): Float =
        com.itzsuli.todaysquote.data.TextSizer.quoteTextSize(length, widthDp, heightDp)

    // ------------------------------------------------------------------ pending intents

    private fun openAppPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context, widgetId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun shufflePendingIntent(context: Context, widgetId: Int): PendingIntent {
        val intent = Intent(context, QuoteWidgetProvider::class.java).apply {
            action = QuoteWidgetProvider.ACTION_SHUFFLE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            // Extras alone don't make PendingIntents distinct; the data URI does.
            data = android.net.Uri.parse("todaysquote://shuffle/$widgetId")
        }
        return PendingIntent.getBroadcast(
            context, widgetId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
