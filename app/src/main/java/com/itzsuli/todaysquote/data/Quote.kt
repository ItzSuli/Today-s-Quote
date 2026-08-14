package com.itzsuli.todaysquote.data

import kotlinx.serialization.Serializable

/**
 * A single quote. Built-in quotes ship with the app and use stable `b:` ids so that
 * favourites and hidden flags survive updates. Custom quotes use `c:` ids.
 */
@Serializable
data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val category: Category = Category.MIND,
    val isCustom: Boolean = false
) {
    /** Author line as shown in the UI, tolerating custom quotes with no author. */
    val displayAuthor: String get() = author.ifBlank { "Unknown" }
}

@Serializable
enum class Category(val label: String) {
    DISCIPLINE("Discipline"),
    ADVERSITY("Adversity"),
    SOLITUDE("Solitude"),
    MORTALITY("Mortality"),
    POWER("Power"),
    TRUTH("Truth"),
    MIND("Mind"),
    CRAFT("Craft"),
    FREEDOM("Freedom");

    companion object {
        fun fromName(name: String?): Category =
            entries.firstOrNull { it.name == name } ?: MIND
    }
}
