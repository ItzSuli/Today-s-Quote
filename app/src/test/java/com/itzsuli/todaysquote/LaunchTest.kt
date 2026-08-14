package com.itzsuli.todaysquote

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.itzsuli.todaysquote.data.BuiltInQuotes
import com.itzsuli.todaysquote.data.QuoteRepository
import com.itzsuli.todaysquote.ui.LibraryScreen
import com.itzsuli.todaysquote.ui.SettingsScreen
import com.itzsuli.todaysquote.ui.TodayScreen
import com.itzsuli.todaysquote.ui.TodaysQuoteTheme
import com.itzsuli.todaysquote.ui.WidgetPreview
import com.itzsuli.todaysquote.ui.WidgetStudioScreen
import com.itzsuli.todaysquote.widget.WidgetSettings
import com.itzsuli.todaysquote.widget.WidgetTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Guards the launch path. v1.0.0 shipped a crash on open — `painterResource` cannot load the
 * `<shape>` drawables the skins are built from — which no amount of unit testing the logic
 * would have caught. Every screen is composed here so that class of failure can't ship again.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LaunchTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun render(content: @Composable () -> Unit) {
        compose.activity.setContent { TodaysQuoteTheme(darkTheme = true) { content() } }
        compose.waitForIdle()
    }

    @Test
    fun `the app opens`() {
        compose.waitForIdle()
    }

    @Test
    fun `every skin renders`() {
        render {
            Column {
                WidgetTheme.entries.forEach { theme ->
                    WidgetPreview(
                        settings = WidgetSettings(theme = theme),
                        quote = BuiltInQuotes.all.first()
                    )
                }
            }
        }
    }

    @Test
    fun `a skin renders at every opacity`() {
        render {
            Column {
                listOf(0, 15, 50, 82, 100).forEach { opacity ->
                    WidgetPreview(
                        settings = WidgetSettings(opacity = opacity),
                        quote = BuiltInQuotes.all.first()
                    )
                }
            }
        }
    }

    @Test
    fun `the preview survives an empty pool`() {
        render { WidgetPreview(settings = WidgetSettings(), quote = null) }
    }

    @Test
    fun `today screen renders`() {
        render { TodayScreen(QuoteRepository.get(compose.activity)) }
    }

    @Test
    fun `library screen renders`() {
        render { LibraryScreen(QuoteRepository.get(compose.activity)) }
    }

    @Test
    fun `widget studio renders`() {
        render { WidgetStudioScreen(repo = QuoteRepository.get(compose.activity)) }
    }

    @Test
    fun `settings screen renders`() {
        render { SettingsScreen(QuoteRepository.get(compose.activity)) }
    }
}
