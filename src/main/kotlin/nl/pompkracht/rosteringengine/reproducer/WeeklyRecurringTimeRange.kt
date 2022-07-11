package nl.pompkracht.rosteringengine.reproducer

import java.time.Instant
import java.time.ZoneId

data class WeeklyRecurringTimeRange(val start: WeeklyRecurringTime, val end: WeeklyRecurringTime) {

    fun subtractedFrom(dateTimeRange: DateTimeRange, timeZone: ZoneId): List<DateTimeRange> {
        val timeLine = overlapTimeLine(dateTimeRange, timeZone)
        return timeLine.mapIndexedNotNull { index, item ->
            val nextIndex = index + 1
            if (!item.active && (nextIndex < timeLine.size)) {
                DateTimeRange(item.instant, timeLine[nextIndex].instant)
            } else {
                null
            }
        }
    }


    private fun overlapTimeLine(dateTimeRange: DateTimeRange, timeZone: ZoneId): List<DateTimeRangeOverlapItem> {
        val dateTimeRangeStart = dateTimeRange.start
        val dateTimeRangeEnd = dateTimeRange.end
        var startOccurrence = start.lastOccurrenceOnOrBefore(dateTimeRangeStart, timeZone)
        var endOccurrence = end.lastOccurrenceOnOrBefore(dateTimeRangeStart, timeZone)

        fun Instant.plusOneWeek() = atZone(timeZone).plusWeeks(1).toInstant()

        val timeLine = mutableListOf<DateTimeRangeOverlapItem>()

        fun nextOverlapItem(): DateTimeRangeOverlapItem {
            val itemInstant = minOf(maxOf(minOf(startOccurrence, endOccurrence), dateTimeRangeStart), dateTimeRangeEnd)
            val active = if (itemInstant == dateTimeRangeStart) {
                startOccurrence >= endOccurrence
            } else if (itemInstant < dateTimeRangeEnd) {
                itemInstant == startOccurrence
            } else {
                false
            }
            if (itemInstant >= startOccurrence) {
                startOccurrence = startOccurrence.plusOneWeek()
            }
            if (itemInstant >= endOccurrence) {
                endOccurrence = endOccurrence.plusOneWeek()
            }
            return DateTimeRangeOverlapItem(itemInstant, active)
        }
        do {
            timeLine.add(nextOverlapItem())
        } while (timeLine.last().instant < dateTimeRangeEnd)
        return timeLine
    }


    private data class DateTimeRangeOverlapItem(val instant: Instant, val active: Boolean)

}
