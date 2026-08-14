package com.itzsuli.todaysquote.data

import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * Picks the quote of the day. The choice is *derived*, never stored: the same day, pool and
 * widget always resolve to the same quote, so a redraw at any hour is stable, and midnight
 * moves everyone on without any bookkeeping.
 */
object DailyPicker {

    /** Local days since the epoch. */
    fun today(nowMillis: Long = System.currentTimeMillis()): Long {
        val offset = java.util.TimeZone.getDefault().getOffset(nowMillis)
        return TimeUnit.MILLISECONDS.toDays(nowMillis + offset)
    }

    /**
     * @param salt distinguishes widgets from each other, so two widgets on the same screen
     *   don't sit there showing the same line.
     * @param shuffleOffset bumped by the user tapping shuffle; folds into the seed.
     */
    fun pick(pool: List<Quote>, day: Long, salt: Int = 0, shuffleOffset: Int = 0): Quote? {
        if (pool.isEmpty()) return null
        val seed = day * 31L + salt * 7919L + shuffleOffset * 104729L
        val index = Random(seed).nextInt(pool.size)
        return pool[index]
    }

    /** Milliseconds until the next local midnight, used to schedule the rollover. */
    fun millisUntilNextMidnight(nowMillis: Long = System.currentTimeMillis()): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(java.util.Calendar.DAY_OF_YEAR, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 5)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return (calendar.timeInMillis - nowMillis).coerceAtLeast(1_000L)
    }
}
