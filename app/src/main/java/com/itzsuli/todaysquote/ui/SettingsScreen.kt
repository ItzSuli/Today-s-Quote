package com.itzsuli.todaysquote.ui

import android.app.TimePickerDialog
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.itzsuli.todaysquote.data.BuiltInQuotes
import com.itzsuli.todaysquote.data.QuoteRepository
import com.itzsuli.todaysquote.notify.DailyNotifier
import com.itzsuli.todaysquote.widget.QuoteWidgetProvider

@Composable
fun SettingsScreen(repo: QuoteRepository) {
    val context = LocalContext.current
    val custom by repo.custom.collectAsState()
    val favourites by repo.favourites.collectAsState()
    val hidden by repo.hidden.collectAsState()

    var notifyOn by remember { mutableStateOf(DailyNotifier.isEnabled(context)) }
    var notifyHour by remember { mutableStateOf(DailyNotifier.hour(context)) }
    var notifyMinute by remember { mutableStateOf(DailyNotifier.minute(context)) }
    var importOpen by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notifyOn = granted
        DailyNotifier.configure(context, granted, notifyHour, notifyMinute)
        if (!granted) message = "Notifications are blocked for this app in Android settings."
    }

    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val raw = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
            }.getOrNull()
            if (raw.isNullOrBlank()) {
                message = "Couldn't read that file."
            } else {
                val added = repo.importJson(raw)
                QuoteWidgetProvider.refreshAll(context)
                message = if (added == 0) "Nothing new to import." else "Imported $added quotes."
            }
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
            "Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(18.dp))
        Card {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Daily notification", color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "One quote a day, nothing else.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = notifyOn,
                    onCheckedChange = { wanted ->
                        if (wanted && !DailyNotifier.hasPermission(context)) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            notifyOn = wanted
                            DailyNotifier.configure(context, wanted, notifyHour, notifyMinute)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0A0D14),
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = Color(0x1FFFFFFF)
                    )
                )
            }
            if (notifyOn) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Arrives at %02d:%02d — tap to change".format(notifyHour, notifyMinute),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                notifyHour = hour
                                notifyMinute = minute
                                DailyNotifier.configure(context, true, hour, minute)
                            },
                            notifyHour, notifyMinute, true
                        ).show()
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Card {
            Text("Your library", color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Stat("Built in", "${BuiltInQuotes.all.size}")
            Stat("Yours", "${custom.size}")
            Stat("Favourites", "${favourites.size}")
            Stat("Hidden", "${hidden.size}")
        }

        Spacer(Modifier.height(12.dp))
        Card {
            Text("Backup", color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))
            Text(
                "Your own quotes only — the built-in ones always come with the app.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PillButton("Export") {
                    if (custom.isEmpty()) {
                        message = "You haven't added any quotes yet."
                    } else {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_TEXT, repo.exportJson())
                            putExtra(Intent.EXTRA_SUBJECT, "todays-quote-backup.json")
                        }
                        context.startActivity(Intent.createChooser(intent, "Export quotes"))
                    }
                }
                PillButton("Import file") { fileLauncher.launch("*/*") }
                PillButton("Paste") { importOpen = true }
            }
        }

        if (hidden.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Card {
                Text(
                    "${hidden.size} built-in quotes are hidden",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))
                PillButton("Bring them all back") {
                    hidden.toList().forEach { repo.setHidden(it, false) }
                    QuoteWidgetProvider.refreshAll(context)
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Today's Quote · one quote a day, chosen from the day itself. No account, no tracking, no network access.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (importOpen) {
        var pasted by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { importOpen = false },
            containerColor = Color(0xFF141A26),
            shape = RoundedCornerShape(26.dp),
            title = { Text("Paste quotes") },
            text = {
                Column {
                    Text(
                        "One per line, as \"quote — author\". A JSON backup works too.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pasted,
                        onValueChange = { pasted = it },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val added = repo.importJson(pasted)
                    QuoteWidgetProvider.refreshAll(context)
                    message = if (added == 0) "Nothing new to import." else "Added $added quotes."
                    importOpen = false
                }) { Text("Import") }
            },
            dismissButton = {
                TextButton(onClick = { importOpen = false }) { Text("Cancel") }
            }
        )
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = { message = null },
            containerColor = Color(0xFF141A26),
            shape = RoundedCornerShape(26.dp),
            text = { Text(text) },
            confirmButton = { TextButton(onClick = { message = null }) { Text("OK") } }
        )
    }
}

@Composable
private fun Card(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0x0FFFFFFF))
            .padding(18.dp),
        content = content
    )
}

@Composable
private fun Stat(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun PillButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x14FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    )
}
