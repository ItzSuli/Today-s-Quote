package com.itzsuli.todaysquote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itzsuli.todaysquote.data.Category
import com.itzsuli.todaysquote.data.DailyPicker
import com.itzsuli.todaysquote.data.QuoteRepository
import com.itzsuli.todaysquote.widget.QuoteSource
import com.itzsuli.todaysquote.widget.TapAction
import com.itzsuli.todaysquote.widget.WidgetAlign
import com.itzsuli.todaysquote.widget.WidgetFont
import com.itzsuli.todaysquote.widget.WidgetPrefs
import com.itzsuli.todaysquote.widget.WidgetRenderer
import com.itzsuli.todaysquote.widget.WidgetSettings
import com.itzsuli.todaysquote.widget.WidgetTheme

/**
 * The customisation surface. Used both as the Widgets tab (editing the defaults that new
 * widgets inherit) and as the configuration screen shown when a widget is placed.
 */
@Composable
fun WidgetStudioScreen(
    repo: QuoteRepository,
    widgetId: Int = WidgetPrefs.DEFAULTS_ID,
    onDone: ((WidgetSettings) -> Unit)? = null
) {
    val context = LocalContext.current
    val custom by repo.custom.collectAsState()
    val favourites by repo.favourites.collectAsState()

    var settings by remember { mutableStateOf(WidgetPrefs.load(context, widgetId)) }
    var previewShuffle by remember { mutableStateOf(0) }

    val placed = remember(settings) { WidgetRenderer.placedWidgetIds(context).size }
    val pool = remember(settings, custom, favourites) {
        WidgetRenderer.pool(context, settings)
    }
    val quote = remember(pool, previewShuffle, settings.shuffleOffset) {
        DailyPicker.pick(pool, DailyPicker.today(), 0, settings.shuffleOffset + previewShuffle)
    }

    fun update(next: WidgetSettings) {
        settings = next
        if (onDone == null) {
            // Editing from the app: save as the default for new widgets and restyle the
            // ones already on the home screen, so what you see here is what you get.
            WidgetPrefs.saveDefaults(context, next)
            WidgetRenderer.applyToAll(context, next)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 40.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (onDone == null) "Widgets" else "Set up your widget",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (onDone == null) {
                "Style every widget from here. To give one widget its own look, long-press it on the home screen instead."
            } else {
                "Tune it now — you can always long-press the widget later to come back here."
            },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .clickable { previewShuffle++ }
        ) {
            WidgetPreview(settings = settings, quote = quote, height = 190.dp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "Tap the preview to see another quote · ${pool.size} in rotation",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        SectionLabel("Skin")
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WidgetTheme.entries.forEach { theme ->
                ThemeSwatch(
                    theme = theme,
                    selected = settings.theme == theme,
                    onClick = { update(settings.copy(theme = theme)) }
                )
            }
        }
        Text(
            settings.theme.blurb,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )

        if (!settings.theme.bare) {
            SectionLabel("Opacity · ${settings.opacity}%")
            TunedSlider(
                value = settings.opacity.toFloat(),
                range = 15f..100f,
                onChange = { update(settings.copy(opacity = it.toInt())) }
            )
        }

        SectionLabel("Text size · ${(settings.textScale * 100).toInt()}%")
        TunedSlider(
            value = settings.textScale,
            range = 0.75f..1.5f,
            onChange = { update(settings.copy(textScale = it)) }
        )

        SectionLabel("Typeface")
        SegmentedRow(
            options = WidgetFont.entries.map { it.label },
            selectedIndex = WidgetFont.entries.indexOf(settings.font),
            onSelect = { update(settings.copy(font = WidgetFont.entries[it])) }
        )

        SectionLabel("Alignment")
        SegmentedRow(
            options = WidgetAlign.entries.map { it.label },
            selectedIndex = WidgetAlign.entries.indexOf(settings.align),
            onSelect = { update(settings.copy(align = WidgetAlign.entries[it])) }
        )

        SectionLabel("Draw from")
        SegmentedRow(
            options = QuoteSource.entries.map { it.label },
            selectedIndex = QuoteSource.entries.indexOf(settings.source),
            onSelect = { update(settings.copy(source = QuoteSource.entries[it])) },
            wrap = true
        )

        SectionLabel("Themes to include")
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = settings.categories.isEmpty(),
                onClick = { update(settings.copy(categories = emptySet())) },
                label = { Text("Everything", fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                border = null,
                colors = chipColors()
            )
            Category.entries.forEach { category ->
                FilterChip(
                    selected = category in settings.categories,
                    onClick = {
                        val next = settings.categories.toMutableSet().apply {
                            if (!add(category)) remove(category)
                        }
                        update(settings.copy(categories = next))
                    },
                    label = { Text(category.label, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    border = null,
                    colors = chipColors()
                )
            }
        }

        SectionLabel("Tapping the widget")
        SegmentedRow(
            options = TapAction.entries.map { it.label },
            selectedIndex = TapAction.entries.indexOf(settings.tapAction),
            onSelect = { update(settings.copy(tapAction = TapAction.entries[it])) },
            wrap = true
        )

        Spacer(Modifier.height(18.dp))
        ToggleRow("Show the author", settings.showAuthor) {
            update(settings.copy(showAuthor = it))
        }
        ToggleRow("Show the shuffle button", settings.showShuffle) {
            update(settings.copy(showShuffle = it))
        }

        if (onDone != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onDone(settings) },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color(0xFF0A0D14)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text("Add widget", fontSize = 16.sp) }
        } else {
            Spacer(Modifier.height(22.dp))
            Text(
                text = placementHint(placed),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun placementHint(placed: Int): String = if (placed == 0) {
    "No widgets placed yet. Long-press an empty spot on your home screen → Widgets → Today\'s Quote, then drag it where you want it."
} else {
    "$placed widget${if (placed == 1) "" else "s"} on your home screen. Drag its handles to resize — the type resizes with it."
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color(0x14FFFFFF),
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = Color(0xFF0A0D14)
)

@Composable
private fun SectionLabel(text: String) {
    Spacer(Modifier.height(20.dp))
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun ThemeSwatch(theme: WidgetTheme, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .width(66.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x0DFFFFFF))
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color(0x1FFFFFFF),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (!theme.bare) {
                DrawableImage(
                    resId = theme.background,
                    alpha = 255,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }
            Text(
                "Aa",
                color = Color(theme.textColor),
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(
            theme.label,
            fontSize = 10.sp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TunedSlider(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit
) {
    Slider(
        value = value.coerceIn(range),
        valueRange = range,
        onValueChange = onChange,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = Color(0x1FFFFFFF)
        )
    )
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SegmentedRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    wrap: Boolean = false
) {
    val content = @Composable {
        options.forEachIndexed { index, label ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                label = { Text(label, fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                border = null,
                colors = chipColors()
            )
        }
    }
    if (wrap) {
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) { content() }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF0A0D14),
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedTrackColor = Color(0x1FFFFFFF)
            )
        )
    }
}
