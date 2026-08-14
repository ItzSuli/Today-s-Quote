package com.itzsuli.todaysquote.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Single source of truth for quotes. Deliberately small: a JSON blob in SharedPreferences
 * is plenty for a few thousand quotes and keeps the widget path synchronous, which matters
 * because [android.appwidget.AppWidgetProvider.onUpdate] has no coroutine scope to wait on.
 */
class QuoteRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val _custom = MutableStateFlow(readCustom())
    val custom: StateFlow<List<Quote>> = _custom.asStateFlow()

    private val _favourites = MutableStateFlow(readStringSet(KEY_FAVOURITES))
    val favourites: StateFlow<Set<String>> = _favourites.asStateFlow()

    private val _hidden = MutableStateFlow(readStringSet(KEY_HIDDEN))
    val hidden: StateFlow<Set<String>> = _hidden.asStateFlow()

    /** Every quote the user can currently see, built-ins minus the ones they've hidden. */
    val allVisible: List<Quote>
        get() {
            val hiddenIds = _hidden.value
            return BuiltInQuotes.all.filterNot { it.id in hiddenIds } + _custom.value
        }

    /** Built-ins included even when hidden, used by the library screen so they can be un-hidden. */
    val everything: List<Quote> get() = BuiltInQuotes.all + _custom.value

    fun quoteById(id: String): Quote? =
        BuiltInQuotes.byId[id] ?: _custom.value.firstOrNull { it.id == id }

    // ------------------------------------------------------------------ custom quotes

    fun addCustom(text: String, author: String, category: Category): Quote {
        val quote = Quote(
            id = "c:${UUID.randomUUID()}",
            text = text.trim(),
            author = author.trim(),
            category = category,
            isCustom = true
        )
        writeCustom(_custom.value + quote)
        return quote
    }

    fun updateCustom(quote: Quote) {
        writeCustom(_custom.value.map { if (it.id == quote.id) quote else it })
    }

    fun deleteCustom(id: String) {
        writeCustom(_custom.value.filterNot { it.id == id })
        setFavourite(id, false)
    }

    /** Used by import. Skips quotes whose text already exists so re-importing is safe. */
    fun addAll(quotes: List<Quote>): Int {
        val existing = _custom.value.map { it.text.trim().lowercase() }.toSet()
        val fresh = quotes
            .filter { it.text.isNotBlank() && it.text.trim().lowercase() !in existing }
            .distinctBy { it.text.trim().lowercase() }
            .map { it.copy(id = "c:${UUID.randomUUID()}", isCustom = true) }
        if (fresh.isNotEmpty()) writeCustom(_custom.value + fresh)
        return fresh.size
    }

    fun exportJson(): String = json.encodeToString(_custom.value)

    fun importJson(raw: String): Int {
        val parsed = runCatching { json.decodeFromString<List<Quote>>(raw) }.getOrElse {
            // Also accept a plain list of strings, one quote per line, "text — author".
            raw.lines().mapNotNull(::parsePlainLine)
        }
        return addAll(parsed)
    }

    private fun parsePlainLine(line: String): Quote? {
        val trimmed = line.trim()
        if (trimmed.isBlank()) return null
        val split = trimmed.split(" — ", " - ", limit = 2)
        return Quote(
            id = "",
            text = split[0].trim().trim('"'),
            author = split.getOrNull(1)?.trim().orEmpty(),
            isCustom = true
        )
    }

    // --------------------------------------------------------------------- favourites

    fun isFavourite(id: String): Boolean = id in _favourites.value

    fun setFavourite(id: String, favourite: Boolean) {
        val next = _favourites.value.toMutableSet().apply {
            if (favourite) add(id) else remove(id)
        }
        _favourites.value = next
        prefs.edit().putStringSet(KEY_FAVOURITES, next).apply()
    }

    fun toggleFavourite(id: String) = setFavourite(id, !isFavourite(id))

    // ------------------------------------------------------------------------- hiding

    fun setHidden(id: String, hidden: Boolean) {
        val next = _hidden.value.toMutableSet().apply {
            if (hidden) add(id) else remove(id)
        }
        _hidden.value = next
        prefs.edit().putStringSet(KEY_HIDDEN, next).apply()
    }

    fun isHidden(id: String): Boolean = id in _hidden.value

    // -------------------------------------------------------------------- persistence

    private fun readCustom(): List<Quote> {
        val raw = prefs.getString(KEY_CUSTOM, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<Quote>>(raw) }.getOrDefault(emptyList())
    }

    private fun writeCustom(list: List<Quote>) {
        _custom.value = list
        prefs.edit().putString(KEY_CUSTOM, json.encodeToString(list)).apply()
    }

    private fun readStringSet(key: String): Set<String> =
        prefs.getStringSet(key, emptySet())?.toSet() ?: emptySet()

    companion object {
        private const val PREFS = "quotes"
        private const val KEY_CUSTOM = "custom_quotes"
        private const val KEY_FAVOURITES = "favourites"
        private const val KEY_HIDDEN = "hidden"

        @Volatile private var instance: QuoteRepository? = null

        fun get(context: Context): QuoteRepository =
            instance ?: synchronized(this) {
                instance ?: QuoteRepository(context).also { instance = it }
            }
    }
}
