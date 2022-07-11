package nl.pompkracht.rosteringengine.reproducer

import java.time.*

private val comparator = compareBy<WeeklyRecurringTime> { it.dayOfWeek }.thenBy { it.timeOfDay }

data class WeeklyRecurringTime(val dayOfWeek: DayOfWeek, val timeOfDay: LocalTime) : Comparable<WeeklyRecurringTime> {
    override operator fun compareTo(other: WeeklyRecurringTime): Int {
        return comparator.compare(this, other)
    }

    fun lastOccurrenceOnOrBefore(instant: Instant, timeZone: ZoneId): Instant {
        val instantLocalDateTime = instant.atZone(timeZone).toLocalDateTime()
        val instantDayOfWeek = instantLocalDateTime.dayOfWeek
        val instantLocalDate = instantLocalDateTime.toLocalDate()
        val instantLocalTime = instantLocalDateTime.toLocalTime()

        val daysDiff = dayOfWeek.ordinal - instantDayOfWeek.ordinal

        val occurrenceDate: LocalDate = if (daysDiff < 0 || (daysDiff == 0 && timeOfDay <= instantLocalTime)) {
            instantLocalDate.plusDays(daysDiff.toLong())
        } else {
            instantLocalDate.minusDays(7 - daysDiff.toLong())
        }
        return occurrenceDate.atTime(timeOfDay).atZone(timeZone).toInstant()
    }


}
