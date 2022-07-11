package nl.pompkracht.rosteringengine.reproducer

import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalUnit

data class DateTimeRange(val start: Instant, val end: Instant) {
    init {
        require(!start.isAfter(end)) { "The value of `end` must be equal to, or greater than the value of `start`" }
    }

    fun overlapsWith(other: DateTimeRange): Boolean = other.end > this.start && other.start < this.end

    fun duration(unit: TemporalUnit): Duration = Duration.of(start.until(end, unit), unit)
}
