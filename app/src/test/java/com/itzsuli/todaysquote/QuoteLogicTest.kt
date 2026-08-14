package com.itzsuli.todaysquote

import com.itzsuli.todaysquote.data.BuiltInQuotes
import com.itzsuli.todaysquote.data.DailyPicker
import com.itzsuli.todaysquote.data.TextSizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteLogicTest {

    private val pool = BuiltInQuotes.all

    @Test
    fun `same day and widget always resolves to the same quote`() {
        val day = DailyPicker.today()
        repeat(50) {
            assertEquals(
                DailyPicker.pick(pool, day, salt = 7, shuffleOffset = 2),
                DailyPicker.pick(pool, day, salt = 7, shuffleOffset = 2)
            )
        }
    }

    @Test
    fun `the quote moves on over a run of days`() {
        val day = DailyPicker.today()
        val seen = (0 until 30).map { DailyPicker.pick(pool, day + it)!!.id }.toSet()
        // Not all 30 need differ, but a stuck picker would be obvious.
        assertTrue("picker looks stuck: $seen", seen.size >= 20)
    }

    @Test
    fun `two widgets on the same day mostly show different quotes`() {
        val day = DailyPicker.today()
        val differing = (1..40).count {
            DailyPicker.pick(pool, day, salt = it) != DailyPicker.pick(pool, day, salt = it + 1)
        }
        assertTrue("salts collapse together: $differing/40", differing >= 35)
    }

    @Test
    fun `shuffling changes the quote`() {
        val day = DailyPicker.today()
        val base = DailyPicker.pick(pool, day, salt = 3, shuffleOffset = 0)
        val shuffled = (1..10).map { DailyPicker.pick(pool, day, salt = 3, shuffleOffset = it) }
        assertTrue(shuffled.any { it != base })
    }

    @Test
    fun `an empty pool yields nothing rather than crashing`() {
        assertNull(DailyPicker.pick(emptyList(), DailyPicker.today()))
        assertNotNull(DailyPicker.pick(listOf(pool.first()), DailyPicker.today()))
    }

    @Test
    fun `midnight is always ahead of us and within a day`() {
        val gap = DailyPicker.millisUntilNextMidnight()
        assertTrue(gap > 0)
        assertTrue(gap <= 24L * 60 * 60 * 1000 + 10_000)
    }

    @Test
    fun `text size stays inside the legible range`() {
        for (length in listOf(10, 40, 90, 160, 260, 600)) {
            for (width in listOf(120, 180, 250, 340, 640)) {
                for (height in listOf(60, 80, 110, 150, 200, 300)) {
                    val size = TextSizer.quoteTextSize(length, width, height)
                    assertTrue(
                        "out of range at $length/$width/$height -> $size",
                        size >= TextSizer.MIN_SP && size <= TextSizer.MAX_SP
                    )
                }
            }
        }
    }

    @Test
    fun `a longer quote never gets bigger type in the same cell`() {
        var previous = Float.MAX_VALUE
        for (length in 20..400 step 20) {
            val size = TextSizer.quoteTextSize(length, 340, 200)
            assertTrue("size grew at length $length", size <= previous)
            previous = size
        }
    }

    @Test
    fun `every category has quotes behind it`() {
        // An empty category would show as a filter chip that silently matches nothing.
        com.itzsuli.todaysquote.data.Category.entries.forEach { category ->
            val count = pool.count { it.category == category }
            assertTrue("${category.label} has only $count quotes", count >= 5)
        }
    }

    @Test
    fun `built-in library is well formed`() {
        assertTrue("library is too small", pool.size >= 120)
        assertEquals("duplicate ids", pool.size, pool.map { it.id }.toSet().size)
        assertEquals(
            "duplicate quotes",
            pool.size,
            pool.map { it.text.trim().lowercase() }.toSet().size
        )
        assertTrue(pool.all { it.text.isNotBlank() && it.author.isNotBlank() })
        assertTrue("quotes should not end without punctuation", pool.all { it.text.trim().last() in ".?!\"" })
        assertEquals(pool.size, BuiltInQuotes.byId.size)
    }
}
