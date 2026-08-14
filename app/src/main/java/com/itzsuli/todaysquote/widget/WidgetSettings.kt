package com.itzsuli.todaysquote.widget

import android.content.Context
import com.itzsuli.todaysquote.data.Category

/**
 * Per-widget configuration. Every placed widget owns its own copy keyed by appWidgetId,
 * so you can run a big Dark Glass card on the home screen and a bare Ink one elsewhere.
 */
data class WidgetSettings(
    val theme: WidgetTheme = WidgetTheme.DARK_GLASS,
    /** Background opacity, 0..100. Only meaningful for themes that paint a card. */
    val opacity: Int = 82,
    /** Multiplier applied to the computed text size, 0.75..1.5. */
    val textScale: Float = 1.0f,
    val showAuthor: Boolean = true,
    val showShuffle: Boolean = true,
    val font: WidgetFont = WidgetFont.SERIF,
    val align: WidgetAlign = WidgetAlign.START,
    val source: QuoteSource = QuoteSource.ALL,
    /** Empty means "no category filter". */
    val categories: Set<Category> = emptySet(),
    val tapAction: TapAction = TapAction.OPEN_APP,
    /** Incremented each time the user shuffles; folded into the daily seed. */
    val shuffleOffset: Int = 0
)

object WidgetPrefs {

    private const val PREFS = "widgets"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Settings used to seed a newly placed widget, editable in the app under Widgets. */
    fun loadDefaults(context: Context): WidgetSettings = load(context, DEFAULTS_ID)

    fun saveDefaults(context: Context, settings: WidgetSettings) =
        save(context, DEFAULTS_ID, settings)

    fun load(context: Context, widgetId: Int): WidgetSettings {
        val p = prefs(context)
        val k = { key: String -> "w${widgetId}_$key" }
        val fallback = if (widgetId == DEFAULTS_ID) WidgetSettings() else loadDefaults(context)
        if (!p.contains(k("theme"))) return fallback
        return WidgetSettings(
            theme = WidgetTheme.fromName(p.getString(k("theme"), null)),
            opacity = p.getInt(k("opacity"), fallback.opacity),
            textScale = p.getFloat(k("textScale"), fallback.textScale),
            showAuthor = p.getBoolean(k("showAuthor"), fallback.showAuthor),
            showShuffle = p.getBoolean(k("showShuffle"), fallback.showShuffle),
            font = WidgetFont.fromName(p.getString(k("font"), fallback.font.name)),
            align = WidgetAlign.fromName(p.getString(k("align"), fallback.align.name)),
            source = QuoteSource.fromName(p.getString(k("source"), fallback.source.name)),
            categories = p.getStringSet(k("categories"), emptySet())
                .orEmpty()
                .map { Category.fromName(it) }
                .toSet(),
            tapAction = TapAction.fromName(p.getString(k("tap"), fallback.tapAction.name)),
            shuffleOffset = p.getInt(k("shuffleOffset"), 0)
        )
    }

    fun save(context: Context, widgetId: Int, settings: WidgetSettings) {
        val k = { key: String -> "w${widgetId}_$key" }
        prefs(context).edit()
            .putString(k("theme"), settings.theme.name)
            .putInt(k("opacity"), settings.opacity)
            .putFloat(k("textScale"), settings.textScale)
            .putBoolean(k("showAuthor"), settings.showAuthor)
            .putBoolean(k("showShuffle"), settings.showShuffle)
            .putString(k("font"), settings.font.name)
            .putString(k("align"), settings.align.name)
            .putString(k("source"), settings.source.name)
            .putStringSet(k("categories"), settings.categories.map { it.name }.toSet())
            .putString(k("tap"), settings.tapAction.name)
            .putInt(k("shuffleOffset"), settings.shuffleOffset)
            .apply()
    }

    fun delete(context: Context, widgetId: Int) {
        val editor = prefs(context).edit()
        prefs(context).all.keys
            .filter { it.startsWith("w${widgetId}_") }
            .forEach { editor.remove(it) }
        editor.apply()
    }

    /** Pseudo-id holding the defaults that new widgets inherit. */
    const val DEFAULTS_ID = -1
}
