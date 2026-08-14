package com.itzsuli.todaysquote.widget

import androidx.annotation.DrawableRes
import com.itzsuli.todaysquote.R

/**
 * A widget skin. Colours live here (not in XML) so the in-app live preview and the real
 * widget are driven by exactly the same numbers.
 */
enum class WidgetTheme(
    val label: String,
    val blurb: String,
    @DrawableRes val background: Int,
    val textColor: Int,
    val authorColor: Int,
    val accentColor: Int,
    /** True when the theme paints no card at all — text straight onto the wallpaper. */
    val bare: Boolean = false
) {
    DARK_GLASS(
        label = "Dark Glass",
        blurb = "Smoked panel, hairline edge. Built for a dark wallpaper.",
        background = R.drawable.widget_bg_dark_glass,
        textColor = 0xFFF2F4F8.toInt(),
        authorColor = 0xFF9BA6BC.toInt(),
        accentColor = 0xFF6E7C96.toInt()
    ),
    OBSIDIAN(
        label = "Obsidian",
        blurb = "Near-solid black. Disappears into an AMOLED screen.",
        background = R.drawable.widget_bg_obsidian,
        textColor = 0xFFEDEDED.toInt(),
        authorColor = 0xFF8A8A8A.toInt(),
        accentColor = 0xFF5C5C5C.toInt()
    ),
    ASH(
        label = "Ash",
        blurb = "Warm graphite with a soft edge.",
        background = R.drawable.widget_bg_ash,
        textColor = 0xFFF5F1EA.toInt(),
        authorColor = 0xFFAFA79B.toInt(),
        accentColor = 0xFF7E766A.toInt()
    ),
    EMBER(
        label = "Ember",
        blurb = "Dark glass with a low amber burn on the author line.",
        background = R.drawable.widget_bg_ember,
        textColor = 0xFFF6F1EC.toInt(),
        authorColor = 0xFFD9A066.toInt(),
        accentColor = 0xFFB9793A.toInt()
    ),
    SAGE(
        label = "Sage",
        blurb = "Dark glass, muted green accent. Quiet.",
        background = R.drawable.widget_bg_sage,
        textColor = 0xFFEFF3EE.toInt(),
        authorColor = 0xFF9DB8A4.toInt(),
        accentColor = 0xFF6E8B77.toInt()
    ),
    FROST(
        label = "Frost",
        blurb = "Pale glass for light wallpapers.",
        background = R.drawable.widget_bg_frost,
        textColor = 0xFF14181F.toInt(),
        authorColor = 0xFF5A6372.toInt(),
        accentColor = 0xFF8D96A5.toInt()
    ),
    PAPER(
        label = "Paper",
        blurb = "Off-white stock, ink-dark type.",
        background = R.drawable.widget_bg_paper,
        textColor = 0xFF1E1B16.toInt(),
        authorColor = 0xFF6B6154.toInt(),
        accentColor = 0xFF9A8F7D.toInt()
    ),
    INK(
        label = "Ink",
        blurb = "No panel. Just the words on your wallpaper.",
        background = R.drawable.widget_bg_none,
        textColor = 0xFFFFFFFF.toInt(),
        authorColor = 0xFFC9CFDA.toInt(),
        accentColor = 0xFF9AA3B2.toInt(),
        bare = true
    ),
    DYNAMIC(
        label = "Dynamic",
        blurb = "Takes its colour from your wallpaper (Android 12+).",
        background = R.drawable.widget_bg_dynamic,
        textColor = 0xFFF3F1F6.toInt(),
        authorColor = 0xFFB9B2C4.toInt(),
        accentColor = 0xFF8C84A0.toInt()
    );

    companion object {
        fun fromName(name: String?): WidgetTheme =
            entries.firstOrNull { it.name == name } ?: DARK_GLASS
    }
}

enum class WidgetFont(val label: String) {
    SANS("Sans"),
    SERIF("Serif"),
    MONO("Mono");

    companion object {
        fun fromName(name: String?): WidgetFont =
            entries.firstOrNull { it.name == name } ?: SANS
    }
}

enum class WidgetAlign(val label: String) {
    START("Left"),
    CENTER("Centred");

    companion object {
        fun fromName(name: String?): WidgetAlign =
            entries.firstOrNull { it.name == name } ?: START
    }
}

/** Which slice of the library a widget draws from. */
enum class QuoteSource(val label: String) {
    ALL("Everything"),
    BUILT_IN("Built-in only"),
    CUSTOM("My quotes only"),
    FAVOURITES("Favourites only");

    companion object {
        fun fromName(name: String?): QuoteSource =
            entries.firstOrNull { it.name == name } ?: ALL
    }
}

/** What a tap on the widget body does. */
enum class TapAction(val label: String) {
    OPEN_APP("Open the app"),
    SHUFFLE("Show another quote"),
    NOTHING("Nothing");

    companion object {
        fun fromName(name: String?): TapAction =
            entries.firstOrNull { it.name == name } ?: OPEN_APP
    }
}
