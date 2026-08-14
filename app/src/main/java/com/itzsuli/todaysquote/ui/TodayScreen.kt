package com.itzsuli.todaysquote.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itzsuli.todaysquote.data.DailyPicker
import com.itzsuli.todaysquote.data.Quote
import com.itzsuli.todaysquote.data.QuoteRepository
import com.itzsuli.todaysquote.widget.WidgetPrefs
import com.itzsuli.todaysquote.widget.WidgetRenderer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(repo: QuoteRepository) {
    val context = LocalContext.current
    val favourites by repo.favourites.collectAsState()
    val custom by repo.custom.collectAsState()
    var extraShuffle by remember { mutableIntStateOf(0) }

    // Read every recomposition so a change made on the Widgets tab shows up here.
    val settings = WidgetPrefs.loadDefaults(context)
    val quote: Quote? = remember(settings, extraShuffle, custom, favourites) {
        DailyPicker.pick(
            pool = WidgetRenderer.pool(context, settings),
            day = DailyPicker.today(),
            salt = 0,
            shuffleOffset = settings.shuffleOffset + extraShuffle
        )
    }

    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (extraShuffle == 0) "Quote of the day" else "Another one",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = quote,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "quote"
        ) { shown ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(30.dp))
            ) {
                WidgetPreview(settings = settings, quote = shown, height = 300.dp)
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0x1AFFFFFF))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isFavourite = quote != null && quote.id in favourites
            IconButton(onClick = { quote?.let { repo.toggleFavourite(it.id) } }) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = if (isFavourite) Color(0xFFD98A82)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { quote?.let { copy(context, it) } }) {
                Icon(
                    Icons.Outlined.ContentCopy, "Copy",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { quote?.let { share(context, it) } }) {
                Icon(
                    Icons.Outlined.Share, "Share",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { extraShuffle++ }) {
                Icon(
                    Icons.Outlined.Shuffle, "Another quote",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Drawing from ${WidgetRenderer.pool(context, settings).size} quotes",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

internal fun quoteAsText(quote: Quote) = "“${quote.text}”\n— ${quote.displayAuthor}"

private fun copy(context: Context, quote: Quote) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("quote", quoteAsText(quote)))
}

private fun share(context: Context, quote: Quote) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, quoteAsText(quote))
    }
    context.startActivity(Intent.createChooser(intent, "Share quote"))
}
