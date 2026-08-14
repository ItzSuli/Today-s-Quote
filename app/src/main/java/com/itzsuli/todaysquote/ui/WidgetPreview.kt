package com.itzsuli.todaysquote.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itzsuli.todaysquote.data.Quote
import com.itzsuli.todaysquote.widget.WidgetAlign
import com.itzsuli.todaysquote.widget.WidgetFont
import com.itzsuli.todaysquote.widget.WidgetRenderer
import com.itzsuli.todaysquote.widget.WidgetSettings

/**
 * A true-to-life preview of the widget. It reuses the widget's own background drawables and
 * its text-size maths, so what you tune here is what lands on the home screen.
 */
@Composable
fun WidgetPreview(
    settings: WidgetSettings,
    quote: Quote?,
    modifier: Modifier = Modifier,
    height: Dp = 168.dp
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    Modifier.background(androidx.compose.ui.graphics.Color.Transparent)
                )
        ) {
            if (!settings.theme.bare) {
                Image(
                    painter = painterResource(settings.theme.background),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    alpha = settings.opacity.coerceIn(0, 100) / 100f,
                    modifier = Modifier.fillMaxSize()
                )
            }

            val text = quote?.text ?: "Add a quote, or widen this widget's filter."
            // Preview cells are roughly the width of a 4-column widget on a phone.
            val sizeSp = WidgetRenderer.quoteTextSize(
                length = text.length,
                widthDp = 340,
                heightDp = with(density) { height.toPx() / density.density }.toInt()
            ) * settings.textScale

            val alignment =
                if (settings.align == WidgetAlign.CENTER) Alignment.CenterHorizontally
                else Alignment.Start
            val textAlign =
                if (settings.align == WidgetAlign.CENTER) TextAlign.Center else TextAlign.Start

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = alignment
            ) {
                androidx.compose.material3.Text(
                    text = text,
                    color = androidx.compose.ui.graphics.Color(settings.theme.textColor),
                    fontSize = sizeSp.sp,
                    lineHeight = (sizeSp * 1.22f).sp,
                    fontFamily = when (settings.font) {
                        WidgetFont.SANS -> FontFamily.SansSerif
                        WidgetFont.SERIF -> FontFamily.Serif
                        WidgetFont.MONO -> FontFamily.Monospace
                    },
                    textAlign = textAlign,
                    maxLines = 12,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                if (settings.showAuthor && quote != null) {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .width(16.dp)
                                .height(1.dp)
                                .background(
                                    androidx.compose.ui.graphics.Color(settings.theme.accentColor)
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                        androidx.compose.material3.Text(
                            text = quote.displayAuthor.uppercase(),
                            color = androidx.compose.ui.graphics.Color(settings.theme.authorColor),
                            fontSize = (sizeSp * 0.52f).coerceIn(9f, 13f).sp,
                            letterSpacing = 1.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
