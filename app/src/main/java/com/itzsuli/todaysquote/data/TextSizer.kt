package com.itzsuli.todaysquote.data

/**
 * Chooses the quote's type size. Pure maths, no Android types, so the widget renderer and the
 * in-app preview share it exactly — and so it can be unit tested on the JVM.
 */
object TextSizer {

    /**
     * @param length characters in the quote
     * @param widthDp,[heightDp] the cell the launcher has given the widget
     * @return size in sp, always within [MIN_SP]..[MAX_SP]
     */
    fun quoteTextSize(length: Int, widthDp: Int, heightDp: Int): Float {
        var size = when {
            heightDp >= 260 -> 26f
            heightDp >= 200 -> 23f
            heightDp >= 150 -> 20f
            heightDp >= 110 -> 17f
            heightDp >= 80 -> 15f
            else -> 13f
        }
        if (widthDp < 180) size -= 2f
        // Roughly how much room each character gets.
        val roomPerChar = (widthDp * heightDp).toFloat() / length.coerceAtLeast(1)
        when {
            roomPerChar < 60 -> size -= 4f
            roomPerChar < 110 -> size -= 2.5f
            roomPerChar < 200 -> size -= 1f
        }
        return size.coerceIn(MIN_SP, MAX_SP)
    }

    const val MIN_SP = 11f
    const val MAX_SP = 30f
}
