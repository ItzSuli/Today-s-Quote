package com.itzsuli.todaysquote.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itzsuli.todaysquote.data.Category
import com.itzsuli.todaysquote.data.Quote
import com.itzsuli.todaysquote.data.QuoteRepository
import com.itzsuli.todaysquote.widget.QuoteWidgetProvider

private enum class LibraryTab(val label: String) {
    ALL("All"), BUILT_IN("Built-in"), MINE("Mine"), FAVOURITES("Favourites")
}

@Composable
fun LibraryScreen(repo: QuoteRepository) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val custom by repo.custom.collectAsState()
    val favourites by repo.favourites.collectAsState()
    val hidden by repo.hidden.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<Category?>(null) }
    var editing by remember { mutableStateOf<Quote?>(null) }
    var creating by remember { mutableStateOf(false) }

    val tab = LibraryTab.entries[tabIndex]
    val visible = remember(tab, query, category, custom, favourites, hidden) {
        repo.everything
            .filter {
                when (tab) {
                    LibraryTab.ALL -> true
                    LibraryTab.BUILT_IN -> !it.isCustom
                    LibraryTab.MINE -> it.isCustom
                    LibraryTab.FAVOURITES -> it.id in favourites
                }
            }
            .filter { category == null || it.category == category }
            .filter {
                query.isBlank() ||
                    it.text.contains(query, true) ||
                    it.author.contains(query, true)
            }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { creating = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color(0xFF0A0D14)
            ) { Icon(Icons.Outlined.Add, "Add a quote") }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = padding.calculateTopPadding())
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 10.dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search quotes or authors") },
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x14FFFFFF),
                    unfocusedContainerColor = Color(0x14FFFFFF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                LibraryTab.entries.forEachIndexed { index, entry ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(entry.label, fontSize = 13.sp) }
                    )
                }
            }

            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip("All", category == null) { category = null }
                Category.entries.forEach { entry ->
                    CategoryChip(entry.label, category == entry) {
                        category = if (category == entry) null else entry
                    }
                }
            }

            if (visible.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Nothing here yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp, end = 20.dp, bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visible, key = { it.id }) { quote ->
                        QuoteRow(
                            quote = quote,
                            isFavourite = quote.id in favourites,
                            isHidden = quote.id in hidden,
                            onFavourite = {
                                repo.toggleFavourite(quote.id)
                                QuoteWidgetProvider.refreshAll(context)
                            },
                            onEdit = { editing = quote },
                            onDelete = {
                                repo.deleteCustom(quote.id)
                                QuoteWidgetProvider.refreshAll(context)
                            },
                            onToggleHidden = {
                                repo.setHidden(quote.id, quote.id !in hidden)
                                QuoteWidgetProvider.refreshAll(context)
                            }
                        )
                    }
                }
            }
        }
    }

    if (creating) {
        QuoteEditorDialog(
            initial = null,
            onDismiss = { creating = false },
            onSave = { text, author, cat ->
                repo.addCustom(text, author, cat)
                QuoteWidgetProvider.refreshAll(context)
                creating = false
            }
        )
    }

    editing?.let { target ->
        QuoteEditorDialog(
            initial = target,
            onDismiss = { editing = null },
            onSave = { text, author, cat ->
                repo.updateCustom(target.copy(text = text, author = author, category = cat))
                QuoteWidgetProvider.refreshAll(context)
                editing = null
            }
        )
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0x14FFFFFF),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color(0xFF0A0D14)
        ),
        border = null
    )
}

@Composable
private fun QuoteRow(
    quote: Quote,
    isFavourite: Boolean,
    isHidden: Boolean,
    onFavourite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleHidden: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x0FFFFFFF))
            .padding(16.dp)
    ) {
        Text(
            text = quote.text,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            lineHeight = 23.sp,
            color = if (isHidden) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = quote.displayAuthor.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = quote.category.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
            )
            if (isHidden) {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "HIDDEN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD98A82)
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onFavourite, modifier = Modifier.height(32.dp)) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = if (isFavourite) Color(0xFFD98A82)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }, modifier = Modifier.height(32.dp)) {
                    Icon(
                        if (quote.isCustom) Icons.Outlined.Edit
                        else if (isHidden) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    if (quote.isCustom) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                            onClick = { menuOpen = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Outlined.Delete, null) },
                            onClick = { menuOpen = false; onDelete() }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(if (isHidden) "Unhide" else "Hide from rotation") },
                            leadingIcon = {
                                Icon(
                                    if (isHidden) Icons.Outlined.Visibility
                                    else Icons.Outlined.VisibilityOff,
                                    null
                                )
                            },
                            onClick = { menuOpen = false; onToggleHidden() }
                        )
                    }
                }
            }
        }
    }
}
