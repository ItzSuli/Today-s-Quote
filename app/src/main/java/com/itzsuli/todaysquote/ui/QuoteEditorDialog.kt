package com.itzsuli.todaysquote.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itzsuli.todaysquote.data.Category
import com.itzsuli.todaysquote.data.Quote

@Composable
fun QuoteEditorDialog(
    initial: Quote?,
    onDismiss: () -> Unit,
    onSave: (text: String, author: String, category: Category) -> Unit
) {
    var text by remember { mutableStateOf(initial?.text.orEmpty()) }
    var author by remember { mutableStateOf(initial?.author.orEmpty()) }
    var category by remember { mutableStateOf(initial?.category ?: Category.MIND) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141A26),
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                if (initial == null) "New quote" else "Edit quote",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Quote") },
                    placeholder = { Text("The words themselves") },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    placeholder = { Text("Leave blank if it's yours") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "Category",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.entries.forEach { entry ->
                        FilterChip(
                            selected = category == entry,
                            onClick = { category = entry },
                            label = { Text(entry.label, fontSize = 12.sp) },
                            shape = RoundedCornerShape(20.dp),
                            border = null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0x14FFFFFF),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color(0xFF0A0D14)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text.trim(), author.trim(), category) },
                enabled = text.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
