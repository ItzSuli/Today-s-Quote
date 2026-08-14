package com.itzsuli.todaysquote.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Draws an Android drawable resource into a Compose canvas.
 *
 * Compose's own `painterResource` only understands vector drawables and bitmaps — hand it a
 * `<shape>` and it throws. The widget skins are shape drawables (gradient fill, hairline
 * stroke, rounded corners), and rendering the very same drawable the widget uses is the whole
 * point of the preview, so we hand it to the platform to draw instead of re-modelling it.
 *
 * @param alpha 0..255, matching `RemoteViews.setImageAlpha` on the real widget.
 */
@Composable
fun DrawableImage(
    @DrawableRes resId: Int,
    alpha: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // mutate() so setting alpha here can't bleed into other users of the same resource.
    val drawable = remember(resId) { ContextCompat.getDrawable(context, resId)?.mutate() }
    Canvas(modifier) {
        val target = drawable ?: return@Canvas
        target.setBounds(0, 0, size.width.toInt(), size.height.toInt())
        target.alpha = alpha.coerceIn(0, 255)
        drawIntoCanvas { canvas -> target.draw(canvas.nativeCanvas) }
    }
}
